package com.example.mobilproje.model

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val category: EventCategory = EventCategory.OTHER,
    val location: EventLocation = EventLocation(),
    val imageUrl: String = "",
    val organizer: String = "",
    val isFavorite: Boolean = false,
    val rating: Float = 0f,
    val comments: List<Comment> = emptyList(),
    val attendees: List<String> = emptyList()
)

data class EventLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = ""
)

data class Comment(
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val rating: Float = 0f,
    val timestamp: Long = 0
)

enum class EventCategory {
    MUSIC,
    SPORTS,
    ART,
    TECHNOLOGY,
    OTHER
}