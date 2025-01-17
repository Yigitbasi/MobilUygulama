package com.example.mobilproje.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.example.mobilproje.model.Event
import java.text.SimpleDateFormat
import java.util.*

object CalendarHelper {
    fun addEventToCalendar(context: Context, event: Event) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, event.location.address)
            
            // Event zamanını ayarla
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val startTime = dateFormat.parse("${event.date} ${event.time}")?.time ?: System.currentTimeMillis()
            val endTime = startTime + (2 * 60 * 60 * 1000) // 2 saat varsayılan süre
            
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            
            // Hatırlatıcı ekle (30 dakika önce)
            putExtra(CalendarContract.Reminders.MINUTES, 30)
            putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        
        context.startActivity(intent)
    }
}
