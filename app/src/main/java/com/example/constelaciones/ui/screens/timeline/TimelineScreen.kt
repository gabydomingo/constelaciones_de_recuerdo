package com.example.constelaciones.ui.screens.timeline

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.constelaciones.viewmodel.TimelineViewModel
import androidx.navigation.NavController
import java.net.URLEncoder

@Composable
fun TimelineScreen(navController: NavController) {
    val viewModel: TimelineViewModel = viewModel()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val defaultEvents by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val eventsToDisplay = if (searchQuery.isNotBlank()) searchResults else defaultEvents

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF16185C), Color(0xFF00021F))
                    )
                )
                .padding(16.dp)
        ) {
            Text("Línea de Tiempo Astronómica", fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Buscar evento...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color.LightGray,
                    unfocusedPlaceholderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (searchQuery.isNotBlank()) {
                Text(
                    text = "Resultados encontrados: ${searchResults.size}",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn {
                    items(eventsToDisplay.size) { index ->
                        val event = eventsToDisplay[index]
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(Color(0xFF1E1E3F), shape = MaterialTheme.shapes.medium)
                                .clickable {
                                    val encodedImage = URLEncoder.encode(event.imageUrl, "UTF-8")
                                    val encodedDesc = URLEncoder.encode(event.description, "UTF-8")
                                    navController.navigate("eventoDetalle/${event.title}/${event.date}/${encodedImage}/${encodedDesc}")
                                }
                                .padding(12.dp)
                        ) {
                            Text(event.date, color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(event.title, color = Color.White, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = rememberAsyncImagePainter(event.imageUrl),
                                contentDescription = event.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(event.description.take(200) + "...", color = Color.LightGray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
