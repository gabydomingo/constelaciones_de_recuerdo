package com.example.constelaciones.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.example.constelaciones.ui.navigation.BottomNavItem
import com.example.constelaciones.ui.screens.chatScreen.ChatScreen
import com.example.constelaciones.ui.screens.constellation.ConstellationScreen
import com.example.constelaciones.ui.screens.editar.EditMemoryScreen
import com.example.constelaciones.ui.screens.favs.FavoriteScreen
import com.example.constelaciones.ui.screens.home.ApodDetailScreen
import com.example.constelaciones.ui.screens.home.HomeScreen
import com.example.constelaciones.ui.screens.login.LoginScreen
import com.example.constelaciones.ui.screens.nuevoRecuerdo.AddMemoryScreen
import com.example.constelaciones.ui.screens.profile.ProfileScreen
import com.example.constelaciones.ui.screens.splash.SplashScreen
import com.example.constelaciones.ui.screens.timeline.TimelineScreen
import com.example.constelaciones.ui.screens.timeline.timelineDetailRoute
import com.example.constelaciones.viewmodel.ConstellationViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable(BottomNavItem.Home.route) {
            HomeScreen(navController)
        }
        composable(BottomNavItem.Constellation.route) {
            ConstellationScreen(navController)
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(navController)
        }
        composable(BottomNavItem.Add.route) {
            AddMemoryScreen(navController)
        }
        composable("apodDetail") {
            ApodDetailScreen(navController)
        }
        composable("timeline") {
            TimelineScreen(navController)
        }
        timelineDetailRoute(navController)
        composable("chat") {
            ChatScreen()
        }
        composable(BottomNavItem.Favorite.route) {
            FavoriteScreen(navController)
        }

        // ——— Nueva ruta para editar un recuerdo ———
        composable(
            route     = "editMemory/{memoryId}",
            arguments = listOf(navArgument("memoryId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            // 1) Extrae el ID de la memoria
            val memoryId = backStackEntry.arguments!!
                .getString("memoryId")!!

            // 2) Recupera el NavBackStackEntry de la pestaña de constelaciones
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(BottomNavItem.Constellation.route)
            }
            // 3) Obtén la misma instancia de ConstellationViewModel
            val vm: ConstellationViewModel = viewModel(parentEntry)

            // 4) Observa la lista ya cargada
            val memories by vm.memories.collectAsState()
            val memory = memories.find { it.id == memoryId }

            if (memory != null) {
                EditMemoryScreen(
                    navController = navController,
                    memory        = memory,
                    viewModel     = vm
                )
            } else {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
