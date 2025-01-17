package com.example.mobilproje.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilproje.model.Event
import com.example.mobilproje.model.UserProfile
import com.example.mobilproje.model.NotificationPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel private constructor() : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile
    
    private val _favoriteEvents = MutableStateFlow<List<Event>>(emptyList())
    val favoriteEvents: StateFlow<List<Event>> = _favoriteEvents

    init {
        loadUserProfile()
    }

    companion object {
        private var instance: ProfileViewModel? = null
        
        fun getInstance(): ProfileViewModel {
            if (instance == null) {
                instance = ProfileViewModel()
            }
            return instance!!
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            _userProfile.value = UserProfile(
                uid = user.uid,
                email = user.email ?: "",
                displayName = user.displayName ?: "",
                notificationPreferences = NotificationPreferences(
                    enableNotifications = true,
                    newEvents = true,
                    favoriteEventUpdates = true,
                    nearbyEvents = true
                )
            )
        }
    }

    fun updateProfile(profile: UserProfile) {
        _userProfile.value = profile
    }

    fun toggleFavorite(event: Event) {
        val currentEvents = _favoriteEvents.value.toMutableList()
        if (currentEvents.any { it.id == event.id }) {
            currentEvents.removeAll { it.id == event.id }
        } else {
            currentEvents.add(event)
        }
        _favoriteEvents.value = currentEvents
    }

    fun updateNotificationPreferences(preferences: NotificationPreferences) {
        _userProfile.value = _userProfile.value?.copy(notificationPreferences = preferences)
    }

    fun logout() {
        auth.signOut()
        _userProfile.value = null
        _favoriteEvents.value = emptyList()
    }
}
