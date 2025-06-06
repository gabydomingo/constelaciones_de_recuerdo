package com.example.constelaciones.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.constelaciones.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.TextButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodDetailScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val apod by viewModel.apod.collectAsState()
    val translatedExplanation by viewModel.translatedExplanation.collectAsState()
    val isSpanish by viewModel.isSpanish.collectAsState()

    val scrollState = rememberScrollState()
    var isTextExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Descripción del día") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.toggleLanguage() }) {
                        Text(
                            text = if (isSpanish) "IN" else "ES",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
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
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF16185C), Color(0xFF00021F))
                    )
                )
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            apod?.let {
                Text(
                    text = it.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                AsyncImage(
                    model = it.url,
                    contentDescription = "Imagen completa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 400.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // texto segun el idioma
                val explanationText = if (isSpanish) {
                    translatedExplanation ?: it.explanation
                } else {
                    it.explanation
                }

                // definir limite de caracteres para vista previa
                val previewLength = 200
                val showExpandButton = explanationText.length > previewLength

                // mostrar texto completo o recortado
                val displayText = if (isTextExpanded || !showExpandButton) {
                    explanationText
                } else {
                    explanationText.take(previewLength) + "..."
                }

                Text(
                    text = displayText,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )

                // boton de ver mas o menos
                if (showExpandButton) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { isTextExpanded = !isTextExpanded }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isTextExpanded) "Mostrar menos" else "Leer descripción completa",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = if (isTextExpanded)
                                    Icons.Default.KeyboardArrowUp else
                                    Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isTextExpanded) "Contraer" else "Expandir",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

            } ?: Text("No hay datos disponibles", color = Color.White)
        }
    }
}
