package com.example.mobilproje.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val preferredCategories: List<EventCategory> = emptyList(),
    val notificationPreferences: NotificationPreferences = NotificationPreferences(),
    val favoriteEvents: List<String> = emptyList() // Event ID'leri
)

data class NotificationPreferences(
    val enableNotifications: Boolean = true,
    val newEvents: Boolean = true,
    val favoriteEventUpdates: Boolean = true,
    val nearbyEvents: Boolean = true
)
