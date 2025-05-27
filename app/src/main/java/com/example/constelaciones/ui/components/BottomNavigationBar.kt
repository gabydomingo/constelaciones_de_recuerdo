package com.example.constelaciones.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.constelaciones.ui.navigation.BottomNavItem

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Constellation,
        BottomNavItem.Add,
        BottomNavItem.Friends,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = Color(0xFF1A1446),
        contentColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                selected = false,
                onClick = {
                    navController.navigate(item.route)
                }
            )
        }
    }
}
