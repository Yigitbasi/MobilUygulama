package com.example.mobilproje.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobilproje.model.Event
import com.example.mobilproje.model.EventCategory
import com.example.mobilproje.api.EventRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEventClick: (Event) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<EventCategory?>(null) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    val repository = remember { EventRepository() }
    val scope = rememberCoroutineScope()

    // İlk yükleme
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            Log.d("HomeScreen", "API çağrısı başlatılıyor...")
            events = repository.getEvents()
            Log.d("HomeScreen", "API çağrısı başarılı: ${events.size} etkinlik alındı")
        } catch (e: Exception) {
            Log.e("HomeScreen", "API çağrısında hata: ${e.message}", e)
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // Arama ve filtreleme
    fun searchEvents() {
        scope.launch {
            isLoading = true
            try {
                Log.d("HomeScreen", "Arama yapılıyor... Query: $searchQuery, Category: $selectedCategory")
                events = repository.getEvents(
                    keyword = searchQuery.takeIf { it.isNotBlank() },
                    category = selectedCategory
                )
                Log.d("HomeScreen", "Arama başarılı: ${events.size} etkinlik bulundu")
            } catch (e: Exception) {
                Log.e("HomeScreen", "Arama sırasında hata: ${e.message}", e)
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etkinlikler") },
                actions = {
                    IconButton(onClick = { searchEvents() }) {
                        Icon(Icons.Default.Search, contentDescription = "Ara")
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    searchEvents()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Etkinlik ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            ScrollableTabRow(
                selectedTabIndex = EventCategory.values().indexOf(selectedCategory ?: EventCategory.OTHER),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                EventCategory.values().forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { 
                            selectedCategory = if (selectedCategory == category) null else category
                            searchEvents()
                        },
                        text = { Text(category.name) }
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(events) { event ->
                        EventCard(event = event, onClick = onEventClick)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(
    event: Event,
    onClick: (Event) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(event) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.date,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = event.category.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}