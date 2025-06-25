package com.example.constelaciones.ui.screens.favs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.constelaciones.data.model.MemoryModel
import com.example.constelaciones.ui.components.BottomNavigationBar
import com.example.constelaciones.ui.components.EstrellasBackground
import com.example.constelaciones.viewmodel.ConstellationViewModel

@Composable
fun FavoriteScreen(navController: NavController) {
    val viewModel: ConstellationViewModel = viewModel()
    val memories by viewModel.memories.collectAsState()
    var selectedMemory by remember { mutableStateOf<MemoryModel?>(null) }


    // Cargar los recuerdos al entrar
    LaunchedEffect(Unit) {
        viewModel.loadMemories()
    }

    val favoritos = memories.filter { it.isFavorito }

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
            //EstrellasBackground(modifier = Modifier.matchParentSize())

            if (selectedMemory == null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(favoritos) { memory ->
                        Text(
                            text = memory.titulo,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMemory = memory }
                                .padding(vertical = 12.dp)
                        )
                        Divider(color = Color.White.copy(alpha = 0.2f))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(Color(0xFF2D2A6A).copy(alpha = 0.95f), shape = MaterialTheme.shapes.large)
                        .padding(16.dp)
                ) {
                    AsyncImage(
                        model = selectedMemory!!.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = selectedMemory!!.titulo, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Text(text = selectedMemory!!.fecha + " - " + selectedMemory!!.ubicacion, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = selectedMemory!!.descripcion, color = Color.White)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedMemory = null },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}
