package com.example.constelaciones.ui.screens.timeline

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavController

fun NavGraphBuilder.timelineDetailRoute(navController: NavController) {
    composable(
        "eventoDetalle/{title}/{date}/{imageUrlEncoded}/{descriptionEncoded}",
        arguments = listOf(
            navArgument("title") { type = NavType.StringType },
            navArgument("date") { type = NavType.StringType },
            navArgument("imageUrlEncoded") { type = NavType.StringType },
            navArgument("descriptionEncoded") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        EventDetailScreen(
            navController = navController,
            title = backStackEntry.arguments?.getString("title") ?: "",
            date = backStackEntry.arguments?.getString("date") ?: "",
            imageUrlEncoded = backStackEntry.arguments?.getString("imageUrlEncoded") ?: "",
            descriptionEncoded = backStackEntry.arguments?.getString("descriptionEncoded") ?: ""
        )
    }
}
