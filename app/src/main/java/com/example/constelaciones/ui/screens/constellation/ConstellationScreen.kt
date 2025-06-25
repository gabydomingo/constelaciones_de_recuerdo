package com.example.constelaciones.ui.screens.constellation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.constelaciones.data.model.MemoryModel
import com.example.constelaciones.ui.components.BottomNavigationBar
import com.example.constelaciones.ui.components.EstrellasBackground
import com.example.constelaciones.ui.components.MemoryDetailCard
import com.example.constelaciones.viewmodel.ConstellationViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ConstellationScreen(navController: NavController) {
    val viewModel: ConstellationViewModel = viewModel()
    val memories by viewModel.memories.collectAsState()
    val positions by viewModel.positions.collectAsState()

    var selectedMemory by remember { mutableStateOf<MemoryModel?>(null) }

    // Cargamos memorias y posiciones al entrar
    LaunchedEffect(Unit) {
        viewModel.loadMemories()
    }

    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF16185C), Color(0xFF00021F)))
                )
        ) {
            EstrellasBackground(modifier = Modifier.matchParentSize())

            if (memories.isNotEmpty()) {
                DrawConstellation(
                    memories = memories,
                    positions = positions,
                    onMemoryClick = { selectedMemory = it }
                )
            }

            selectedMemory?.let { memory ->
                MemoryDetailCard(
                    memory = memory,
                    onDismiss = { selectedMemory = null },
                    onToggleFavorito = { viewModel.toggleFavorito(memory) }
                )
            }
        }
    }
}

@Composable
fun DrawConstellation(
    memories: List<MemoryModel>,
    positions: Map<String, Offset>,
    onMemoryClick: (MemoryModel) -> Unit
) {
    // guardamos el tamaño de pantalla para escalar
    var screenSize by remember { mutableStateOf(Pair(1f, 1f)) }
    val visibleMemories = remember { mutableStateListOf<MemoryModel>() }

    LaunchedEffect(memories) {
        visibleMemories.clear()
        memories.forEach { memory ->
            delay(100L)
            visibleMemories.add(memory)
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    visibleMemories.forEach { memory ->
                        positions[memory.id]?.let { normPos ->
                            val realPos = Offset(normPos.x * screenSize.first, normPos.y * screenSize.second)
                            if ((tapOffset - realPos).getDistance() < 20f) {
                                onMemoryClick(memory)
                            }
                        }
                    }
                }
            }
    ) {
        // actualizamos el tamaño de pantalla
        screenSize = size.width to size.height

        // convertimos posiciones normalizadas a reales
        val coords = visibleMemories.mapNotNull { memory ->
            positions[memory.id]?.let { norm ->
                Offset(norm.x * size.width, norm.y * size.height)
            }
        }

        // dibujamos líneas
        coords.zipWithNext { a, b ->
            drawLine(
                color = Color.White.copy(alpha = 0.2f),
                start = a,
                end = b,
                strokeWidth = 1.5.dp.toPx()
            )
        }

        // dibujamos las estrellas
        visibleMemories.forEach { memory ->
            positions[memory.id]?.let { norm ->
                drawCircle(
                    color = Color.Yellow.copy(alpha = 0.8f),
                    radius = 6.dp.toPx(),
                    center = Offset(norm.x * size.width, norm.y * size.height)
                )
            }
        }
    }
}

