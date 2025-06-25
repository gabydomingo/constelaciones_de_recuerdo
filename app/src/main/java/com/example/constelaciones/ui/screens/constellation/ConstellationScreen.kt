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
            EstrellasBackground(modifier = Modifier.matchParentSize())

            if (memories.isNotEmpty()) {
                DrawConstellation(memories) { selectedMemory = it }
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
fun DrawConstellation(memories: List<MemoryModel>, onMemoryClick: (MemoryModel) -> Unit) {
    val screenSize = remember { mutableStateOf(Pair(1f, 1f)) }

    val positions = remember(memories) {
        memories.associateWith {
            Offset(Random.nextFloat(), Random.nextFloat())
        }
    }

    val visibleMemories = remember { mutableStateListOf<MemoryModel>() }

    LaunchedEffect(memories) {
        visibleMemories.clear()
        memories.forEach { memory ->
            delay(100L) // delay entre estrellas
            visibleMemories.add(memory)
        }
    }

    Canvas(
        modifier = Modifier
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

        val coords = visibleMemories.mapNotNull { positions[it] }.map {
            Offset(it.x * size.width, it.y * size.height)
        }

        coords.zipWithNext().forEach { (a, b) ->
            drawLine(
                color = Color.White.copy(alpha = 0.2f),
                start = a,
                end = b,
                strokeWidth = 1.5.dp.toPx()
            )
        }

        visibleMemories.forEach { memory ->
            val pos = positions[memory] ?: return@forEach
            val center = Offset(pos.x * size.width, pos.y * size.height)

            drawCircle(
                color = Color.Yellow.copy(alpha = 0.8f),
                radius = 6.dp.toPx(),
                center = center
            )
        }
    }
}