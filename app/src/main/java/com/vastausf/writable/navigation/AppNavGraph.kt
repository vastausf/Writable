package com.vastausf.writable.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.vastausf.writable.ui.screens.editor.EditorScreen
import com.vastausf.writable.ui.screens.home.HomeScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onDocumentClick = { documentId ->
                    navController.navigate(EditorRoute(documentId))
                },
            )
        }
        composable<EditorRoute> { backstackEntry ->
            val route = backstackEntry.toRoute<EditorRoute>()

            EditorScreen(
                documentId = route.documentId,
            )
        }
    }
}
