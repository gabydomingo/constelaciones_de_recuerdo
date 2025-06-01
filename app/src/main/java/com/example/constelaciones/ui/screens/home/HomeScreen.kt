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
import com.example.constelaciones.viewmodel.HomeViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import com.example.constelaciones.ui.components.ScaffoldWithBackground

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val apod by viewModel.apod.collectAsState()
    val imageTitle by viewModel.imageTitle.collectAsState()
    val context = LocalContext.current

    ScaffoldWithBackground(navController = navController) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
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

                // Contenedor dinámico.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .background(Color(0xFF1F1B5E)),
                    contentAlignment = Alignment.Center

                )


                { when (apod?.media_type) {
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
                /// boton linea de tiempo
                Button(
                    onClick = { navController.navigate("timeline") }
                ) {
                    Text("Explorar Línea de Tiempo")
                }

                Spacer(modifier = Modifier.height(16.dp))

                ///boton chat ia
                Button(
                    onClick = { navController.navigate("chat") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                ) {
                    Text("Hablar con la IA", color = Color.White)
                }
            }
        }


    }



}
