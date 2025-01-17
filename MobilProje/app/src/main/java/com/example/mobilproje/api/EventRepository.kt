package com.example.mobilproje.api

import android.util.Log
import com.example.mobilproje.model.Event
import com.example.mobilproje.model.EventCategory
import com.example.mobilproje.model.EventLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class EventRepository {
    private val api = NetworkModule.ticketmasterApi
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    suspend fun getEvents(
        latLong: String? = null,
        keyword: String? = null,
        category: EventCategory? = null,
        page: Int = 0
    ): List<Event> = withContext(Dispatchers.IO) {
        try {
            Log.d("EventRepository", "API çağrısı yapılıyor: latLong=$latLong, keyword=$keyword, category=${category?.name}, page=$page")
            
            // Şu anki tarihi ISO 8601 formatında al
            val currentDateTime = dateFormat.format(Date())
            
            // Kategori adını TicketMaster API formatına dönüştür
            val apiCategory = when (category) {
                EventCategory.MUSIC -> "Music"
                EventCategory.SPORTS -> "Sports"
                EventCategory.ART -> "Arts & Theatre"
                EventCategory.TECHNOLOGY -> null  // Technology için özel bir segment yok, keyword ile arayacağız
                EventCategory.OTHER -> null
                null -> null
            }
            
            // Technology kategorisi için keyword'e "tech" ekle
            val finalKeyword = if (category == EventCategory.TECHNOLOGY) {
                if (keyword.isNullOrEmpty()) "technology" else "$keyword technology"
            } else {
                keyword
            }
            
            val response = api.getEvents(
                latLong = latLong,
                keyword = finalKeyword,
                category = apiCategory,
                page = page,
                startDateTime = currentDateTime // Sadece şu andan sonraki etkinlikleri getir
            )
            Log.d("EventRepository", "API yanıtı alındı: ${response._embedded?.events?.size ?: 0} etkinlik")
            
            response._embedded?.events?.map { it.toEvent() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("EventRepository", "API çağrısında hata: ${e.message}", e)
            throw e
        }
    }

    private fun EventDto.toEvent(): Event {
        return try {
            val venue = this._embedded?.venues?.firstOrNull()
            val location = venue?.location?.let {
                EventLocation(
                    latitude = it.latitude?.toDoubleOrNull() ?: 0.0,
                    longitude = it.longitude?.toDoubleOrNull() ?: 0.0,
                    address = buildString {
                        append(venue.name ?: "")
                        venue.address?.line1?.let { append(", $it") }
                        venue.city?.name?.let { append(", $it") }
                        venue.state?.stateCode?.let { append(", $it") }
                        venue.country?.name?.let { append(", $it") }
                    }
                )
            } ?: EventLocation()

            // Log category information for debugging
            val segmentName = classifications?.firstOrNull()?.segment?.name
            val genreName = classifications?.firstOrNull()?.genre?.name
            Log.d("EventRepository", "Event kategori bilgisi: segment=$segmentName, genre=$genreName")

            Event(
                id = id,
                title = name,
                description = description ?: "",
                date = dates?.start?.localDate ?: "",
                time = dates?.start?.localTime ?: "",
                category = classifications?.firstOrNull()?.let { classification ->
                    val segment = classification.segment?.name?.uppercase()
                    val genre = classification.genre?.name?.uppercase()
                    
                    when {
                        segment == "MUSIC" -> EventCategory.MUSIC
                        segment == "SPORTS" -> EventCategory.SPORTS
                        segment == "ARTS & THEATRE" || segment == "ARTS & THEATER" || 
                        genre?.contains("ART") == true -> EventCategory.ART
                        genre?.contains("TECHNOLOGY") == true || 
                        genre?.contains("TECH") == true -> EventCategory.TECHNOLOGY
                        else -> {
                            Log.d("EventRepository", "Bilinmeyen kategori: segment=$segment, genre=$genre")
                            EventCategory.OTHER
                        }
                    }
                } ?: EventCategory.OTHER,
                location = location,
                imageUrl = images?.firstOrNull()?.url ?: "",
                organizer = _embedded?.venues?.firstOrNull()?.name ?: ""
            )
        } catch (e: Exception) {
            Log.e("EventRepository", "Event dönüşümünde hata: ${e.message}", e)
            throw e
        }
    }
}
