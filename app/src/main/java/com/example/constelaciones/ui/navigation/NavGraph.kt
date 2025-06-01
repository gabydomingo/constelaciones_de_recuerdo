package com.example.constelaciones.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.constelaciones.ui.screens.home.HomeScreen
import com.example.constelaciones.ui.screens.constellation.ConstellationScreen
import com.example.constelaciones.ui.screens.home.ApodDetailScreen
import com.example.constelaciones.ui.screens.login.LoginScreen
import com.example.constelaciones.ui.screens.profile.ProfileScreen
import com.example.constelaciones.ui.screens.splash.SplashScreen
import com.example.constelaciones.ui.screens.nuevoRecuerdo.AddMemoryScreen
import com.example.constelaciones.ui.screens.timeline.TimelineScreen
import com.example.constelaciones.ui.screens.timeline.timelineDetailRoute
import com.example.constelaciones.ui.screens.chat.ChatScreen

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
    }
}
