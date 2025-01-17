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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mobilproje.model.Event
import com.example.mobilproje.model.Comment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,
    onNavigateBack: () -> Unit,
    onToggleFavorite: (Event) -> Unit,
    onAddComment: (Event, String, Float) -> Unit,
    onAttendEvent: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCommentDialog by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event.title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleFavorite(event) }) {
                        Icon(
                            if (event.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (event.isFavorite) "Favorilerden Çıkar" else "Favorilere Ekle"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Etkinlik Detayları
            item {
                EventDetailsSection(event)
            }

            // Katılım ve Hatırlatıcı Butonları
            item {
                ActionButtons(
                    event = event,
                    onAttendEvent = onAttendEvent
                )
            }

            // Puanlama ve Yorumlar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Yorumlar ve Değerlendirmeler",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showCommentDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Yorum Ekle")
                        }
                    }
                }
            }

            // Yorumlar Listesi
            items(event.comments) { comment ->
                CommentItem(comment)
            }
        }

        // Yorum Ekleme Dialog
        if (showCommentDialog) {
            AlertDialog(
                onDismissRequest = { showCommentDialog = false },
                title = { Text("Yorum Ekle") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            label = { Text("Yorumunuz") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Puan")
                        Slider(
                            value = rating,
                            onValueChange = { rating = it },
                            valueRange = 0f..5f,
                            steps = 4
                        )
                        Text("${rating.toInt()} / 5")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onAddComment(event, commentText, rating)
                            commentText = ""
                            rating = 0f
                            showCommentDialog = false
                        }
                    ) {
                        Text("Gönder")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCommentDialog = false }) {
                        Text("İptal")
                    }
                }
            )
        }
    }
}

@Composable
private fun EventDetailsSection(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Etkinlik Bilgileri
            InfoRow(
                icon = Icons.Default.LocationOn,
                text = event.location.address
            )
            InfoRow(
                icon = Icons.Default.DateRange,
                text = "${event.date} ${event.time}"
            )
            InfoRow(
                icon = Icons.Default.Person,
                text = "Organizatör: ${event.organizer}"
            )
            InfoRow(
                icon = Icons.Default.Star,
                text = "Puan: ${event.rating}/5.0"
            )
        }
    }
}

@Composable
private fun ActionButtons(
    event: Event,
    onAttendEvent: (Event) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { onAttendEvent(event) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (event.attendees.isEmpty()) "Katıl" else "Katılıyor")
        }
    }
}

@Composable
private fun CommentItem(comment: Comment) {
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${comment.rating}/5",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis
        )
    }
}
