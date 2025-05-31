package com.example.constelaciones.ui.screens.timeline

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import java.net.URLDecoder
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.constelaciones.viewmodel.EventDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    navController: NavController,
    title: String,
    date: String,
    imageUrlEncoded: String,
    descriptionEncoded: String
) {
    val imageUrl = URLDecoder.decode(imageUrlEncoded, "UTF-8")
    val originalDescription = URLDecoder.decode(descriptionEncoded, "UTF-8")

    val viewModel: EventDetailViewModel = viewModel(
        factory = EventDetailViewModel.Factory(originalDescription)
    )

    val translated by viewModel.translated.collectAsState()
    val isSpanish by viewModel.isSpanish.collectAsState()

    val description = if (isSpanish) translated ?: originalDescription else originalDescription

    var isTextExpanded by remember { mutableStateOf(false) }
    val previewLength = 200
    val showExpandButton = description.length > previewLength
    val displayText = if (isTextExpanded || !showExpandButton) description else description.take(previewLength) + "..."

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Evento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.toggleLanguage() }) {
                        Text(if (isSpanish) "IN" else "ES", color = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFF16185C), Color(0xFF00021F))))
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 22.sp, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(date, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(displayText, fontSize = 16.sp, color = Color.White, lineHeight = 22.sp)

            if (showExpandButton) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp))
                        .clickable { isTextExpanded = !isTextExpanded }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isTextExpanded) "Mostrar menos" else "Leer descripción completa",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isTextExpanded)
                                Icons.Default.KeyboardArrowUp else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expandir",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
