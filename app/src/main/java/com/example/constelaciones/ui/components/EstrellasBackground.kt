package com.example.constelaciones.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun EstrellasBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starCount = 500
            repeat(starCount) {
                val x = Random.nextFloat() * size.width
                val y = Random.nextFloat() * size.height
                val radius = Random.nextFloat() * 2f + 1f
                drawCircle(Color.White.copy(alpha = 0.8f), radius = radius, center = Offset(x, y))
            }
        }
    }
}
