package com.example.mobilproje.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobilproje.model.Event
import com.example.mobilproje.model.NotificationPreferences
import com.example.mobilproje.model.UserProfile
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfile?,
    favoriteEvents: List<Event>,
    onUpdateProfile: (UserProfile) -> Unit,
    onToggleFavorite: (Event) -> Unit,
    onUpdateNotifications: (NotificationPreferences) -> Unit,
    onLogout: () -> Unit,
    onNavigateToEvent: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    if (userProfile == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var editedDisplayName by remember { mutableStateOf(userProfile.displayName) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Profili Düzenle") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedDisplayName,
                        onValueChange = { editedDisplayName = it },
                        label = { Text("Kullanıcı Adı") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(userProfile.copy(displayName = editedDisplayName))
                        showEditDialog = false
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Çıkış Yap")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profil Bilgileri
            item {
                ProfileSection(
                    userProfile = userProfile,
                    onEditClick = { showEditDialog = true }
                )
            }

            // Bildirim Ayarları
            item {
                NotificationSection(
                    preferences = userProfile.notificationPreferences,
                    onPreferencesChanged = onUpdateNotifications
                )
            }

            // Favori Etkinlikler
            item {
                Text(
                    text = "Favori Etkinlikler",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (favoriteEvents.isEmpty()) {
                item {
                    Text(
                        text = "Henüz favori etkinliğiniz yok",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(favoriteEvents) { event ->
                    FavoriteEventCard(
                        event = event,
                        onClick = { onNavigateToEvent(event) },
                        onRemove = { onToggleFavorite(event) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(
    userProfile: UserProfile,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profil Bilgileri",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Düzenle")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("E-posta: ${userProfile.email}")
            Text("Kullanıcı Adı: ${userProfile.displayName}")
        }
    }
}

@Composable
private fun NotificationSection(
    preferences: NotificationPreferences,
    onPreferencesChanged: (NotificationPreferences) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Bildirim Ayarları",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Switch(
                checked = preferences.enableNotifications,
                onCheckedChange = { enabled ->
                    onPreferencesChanged(preferences.copy(enableNotifications = enabled))
                }
            )
            
            if (preferences.enableNotifications) {
                Spacer(modifier = Modifier.height(8.dp))
                CheckboxWithLabel(
                    checked = preferences.newEvents,
                    onCheckedChange = { checked ->
                        onPreferencesChanged(preferences.copy(newEvents = checked))
                    },
                    label = "Yeni Etkinlikler"
                )
                
                CheckboxWithLabel(
                    checked = preferences.favoriteEventUpdates,
                    onCheckedChange = { checked ->
                        onPreferencesChanged(preferences.copy(favoriteEventUpdates = checked))
                    },
                    label = "Favori Etkinlik Güncellemeleri"
                )
                
                CheckboxWithLabel(
                    checked = preferences.nearbyEvents,
                    onCheckedChange = { checked ->
                        onPreferencesChanged(preferences.copy(nearbyEvents = checked))
                    },
                    label = "Yakındaki Etkinlikler"
                )
            }
        }
    }
}

@Composable
private fun CheckboxWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(label)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteEventCard(
    event: Event,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${event.date} ${event.time}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Favorilerden Kaldır")
            }
        }
    }
}
