package com.example.constelaciones.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.constelaciones.ui.components.EstrellasBackground
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SplashScreen(navController: NavController) {
    val points = remember { mutableStateListOf<Offset>() }

    // animacion de opacidad para constelacion generada
    val infiniteTransition = rememberInfiniteTransition()
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // animacion puntos suspensivos
    var dotCount by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500L)
            dotCount = (dotCount + 1) % 4
        }
    }

    // generar constelacion
    LaunchedEffect(Unit) {
        val totalStars = 6
        repeat(totalStars) {
            val newPoint = Offset(
                x = 0.3f + Random.nextFloat() * 0.4f,
                y = 0.3f + Random.nextFloat() * 0.4f
            )
            points.add(newPoint)
            delay(250L)
        }
        delay(1000L)
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // fondo de background estrellado
        EstrellasBackground(modifier = Modifier.matchParentSize())

        // constelacion generada random
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val scaledPoints = points.map {
                Offset(it.x * canvasWidth, it.y * canvasHeight)
            }

            for (i in scaledPoints.indices) {
                drawCircle(
                    color = Color.White.copy(alpha = alphaAnim),
                    radius = 8f, // más grande
                    center = scaledPoints[i]
                )
                if (i > 0) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = scaledPoints[i - 1],
                        end = scaledPoints[i],
                        strokeWidth = 3f // más grueso
                    )
                }
            }
        }

        // texto con puntos suspensivos y sombra
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 150.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "Constelaciones de Recuerdos" + ".".repeat(dotCount),
                color = Color.White,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.White,
                        offset = Offset(1f, 1f),
                        blurRadius = 4f
                    )
                )
            )
        }
    }
}

