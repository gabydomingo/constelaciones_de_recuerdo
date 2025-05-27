package com.example.constelaciones.ui.screens.constellation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.constelaciones.ui.components.BottomNavigationBar
import kotlin.random.Random
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background


@Composable
fun ConstellationScreen(navController: NavController) {
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
                )
        ) {
            StarryBackground() // ahora se dibuja sobre el fondo degradado

            // Aquí irán las estrellas del usuario, botones, etc.
        }
    }
}

@Composable
fun StarryBackground(starCount: Int = 1000) {
    val infiniteTransition = rememberInfiniteTransition()

    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )


    val stars = remember {
        List(starCount) {
            Offset(
                x = Random.nextFloat(),
                y = Random.nextFloat()
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        stars.forEach { star ->
            drawCircle(
                color = Color.White.copy(alpha = alphaAnim),
                radius = 1.5.dp.toPx(),
                center = Offset(star.x * canvasWidth, star.y * canvasHeight)
            )
        }
    }
}
