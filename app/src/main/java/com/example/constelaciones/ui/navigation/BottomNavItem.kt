package com.example.constelaciones.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", Icons.Default.Home)
    object Constellation : BottomNavItem("constellation", Icons.Default.Share)
    object Add : BottomNavItem("add", Icons.Default.Add)
    object Favorite : BottomNavItem("favorite", Icons.Default.StarBorder)
    object Profile : BottomNavItem("profile", Icons.Default.Person)
}
