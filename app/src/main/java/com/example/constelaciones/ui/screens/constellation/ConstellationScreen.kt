package com.example.constelaciones.ui.screens.constellation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.constelaciones.data.model.MemoryModel
import com.example.constelaciones.ui.components.BottomNavigationBar
import com.example.constelaciones.viewmodel.ConstellationViewModel
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun ConstellationScreen(navController: NavController) {
    val viewModel: ConstellationViewModel = viewModel()
    val memories by viewModel.memories.collectAsState()
    var selectedMemory by remember { mutableStateOf<MemoryModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMemories()
    }


    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF16185C), Color(0xFF00021F))
                    )
                )
        ) {
            if (memories.isNotEmpty()) {
                DrawConstellation(memories) { selectedMemory = it }
            }

            selectedMemory?.let { MemoryModel ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF2D2A6A).copy(alpha = 0.95f))
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberAsyncImagePainter(MemoryModel.imageUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(MemoryModel.titulo, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                        Text("${MemoryModel.fecha} - ${MemoryModel.ubicacion}", color = Color.LightGray)
                        Spacer(Modifier.height(8.dp))
                        Text(MemoryModel.descripcion, color = Color.White)
                        Spacer(Modifier.height(8.dp))
                        IconButton(onClick = { selectedMemory = null }, modifier = Modifier.align(Alignment.End)) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawConstellation(memories: List<MemoryModel>, onMemoryClick: (MemoryModel) -> Unit) {
    val screenSize = remember { mutableStateOf(Pair(1f, 1f)) }

    // Generamos posiciones aleatorias para cada recuerdo
    val positions = remember(memories) {
        memories.map {
            it to Offset(Random.nextFloat(), Random.nextFloat())
        }.toMap()
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                positions.forEach { (memory, pos) ->
                    val center = Offset(pos.x * screenSize.value.first, pos.y * screenSize.value.second)
                    if ((offset - center).getDistance() < 20f) {
                        onMemoryClick(memory)
                    }
                }
            }
        }
    ) {
        screenSize.value = size.width to size.height

        // Dibujar líneas
        val coords = positions.values.map {
            Offset(it.x * size.width, it.y * size.height)
        }

        coords.zipWithNext().forEach { (a, b) ->
            drawLine(Color.White.copy(alpha = 0.2f), a, b, strokeWidth = 1.5.dp.toPx())
        }

        // Dibujar puntos
        positions.forEach { (_, pos) ->
            drawCircle(
                color = Color.Yellow.copy(alpha = 0.8f),
                radius = 6.dp.toPx(),
                center = Offset(pos.x * size.width, pos.y * size.height)
            )
        }
    }
}
