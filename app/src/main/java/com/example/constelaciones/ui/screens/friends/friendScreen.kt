package com.example.constelaciones.ui.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.constelaciones.ui.components.ScaffoldWithBackground

///temporal para agregar mas adelante.
@Composable
fun friendScreen(navController: NavController) {
    ScaffoldWithBackground(navController = navController) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

        )}
    }



