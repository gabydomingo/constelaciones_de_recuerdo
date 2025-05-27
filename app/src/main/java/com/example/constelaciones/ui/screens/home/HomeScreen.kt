package com.example.constelaciones.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.constelaciones.ui.components.BottomNavigationBar
import com.example.constelaciones.viewmodel.HomeViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val apod by viewModel.apod.collectAsState()
    val imageTitle by viewModel.imageTitle.collectAsState()
    val context = LocalContext.current

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF16185C), Color(0xFF00021F))
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Título
                Text(
                    text = "Imagen del día",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contenedor dinámico
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .background(Color(0xFF1F1B5E)),
                    contentAlignment = Alignment.Center
                ) {
                    when (apod?.media_type) {
                        "image" -> {
                            AsyncImage(
                                model = apod?.url,
                                contentDescription = "Imagen del día",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable {
                                        apod?.let {
                                            navController.navigate("apodDetail")
                                        }
                                    }
                            )

                        }

                        "video" -> {
                            Text(
                                text = "Tocar para ver el video",
                                color = Color.White,
                                modifier = Modifier
                                    .clickable {
                                        apod?.url?.let {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                            context.startActivity(intent)
                                        }
                                    }
                                    .padding(32.dp)
                            )
                        }

                        else -> {
                            Text(
                                text = "Cargando contenido de la NASA...",
                                color = Color.White,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
