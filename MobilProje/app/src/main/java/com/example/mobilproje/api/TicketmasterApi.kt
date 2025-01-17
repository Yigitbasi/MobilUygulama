package com.example.mobilproje.api

import retrofit2.http.GET
import retrofit2.http.Query

interface TicketmasterApi {
    @GET("discovery/v2/events.json")
    suspend fun getEvents(
        @Query("apikey") apiKey: String = CONSUMER_KEY,
        @Query("latlong") latLong: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("segmentName") category: String? = null,
        @Query("radius") radius: Int = 50,
        @Query("unit") unit: String = "km",
        @Query("size") size: Int = 50,
        @Query("page") page: Int = 0,
        @Query("sort") sort: String = "date,name,asc",
        @Query("startDateTime") startDateTime: String? = null
    ): EventResponse

    companion object {
        const val BASE_URL = "https://app.ticketmaster.com/"
        const val CONSUMER_KEY = "6t0pLGopm2TH5o8Q4Kt5adRM9gqnxGxo"
    }
}

data class EventResponse(
    val _embedded: Embedded? = null,
    val page: Page? = null
)

data class Embedded(
    val events: List<EventDto> = emptyList()
)

data class Page(
    val size: Int = 0,
    val totalElements: Int = 0,
    val totalPages: Int = 0,
    val number: Int = 0
)

data class EventDto(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val dates: Dates? = null,
    val classifications: List<Classification>? = null,
    val _embedded: EventEmbedded? = null,
    val images: List<Image>? = null,
    val priceRanges: List<PriceRange>? = null
)

data class Dates(
    val start: Start? = null,
    val status: Status? = null
)

data class Start(
    val localDate: String? = null,
    val localTime: String? = null,
    val dateTime: String? = null
)

data class Status(
    val code: String? = null
)

data class Classification(
    val segment: Segment? = null,
    val genre: Genre? = null
)

data class Segment(
    val name: String? = null
)

data class Genre(
    val name: String? = null
)

data class EventEmbedded(
    val venues: List<Venue>? = null
)

data class Venue(
    val name: String? = null,
    val city: City? = null,
    val state: State? = null,
    val country: Country? = null,
    val address: Address? = null,
    val location: Location? = null
)

data class City(
    val name: String? = null
)

data class State(
    val name: String? = null,
    val stateCode: String? = null
)

data class Country(
    val name: String? = null,
    val countryCode: String? = null
)

data class Address(
    val line1: String? = null
)

data class Location(
    val latitude: String? = null,
    val longitude: String? = null
)

data class Image(
    val url: String? = null,
    val ratio: String? = null,
    val width: Int? = null,
    val height: Int? = null
)

data class PriceRange(
    val type: String? = null,
    val currency: String? = null,
    val min: Double? = null,
    val max: Double? = null
)
