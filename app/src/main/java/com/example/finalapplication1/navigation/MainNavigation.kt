package com.example.finalapplication1.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.finalapplication1.PictureViewModel
import com.example.finalapplication1.Screens.DetailsScreen
import com.example.finalapplication1.Screens.FavoriteScreen
import com.example.finalapplication1.Screens.MainScreen
import com.example.finalapplication1.Screens.SearchScreen


@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    val viewModel: PictureViewModel = viewModel()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { MainScreen(viewModel) }
            composable(BottomNavItem.Search.route) { SearchScreen(viewModel, navController) }
            composable(BottomNavItem.Favorites.route) { FavoriteScreen(viewModel, navController) }
            composable(
                route = DetailRoute.route,
                arguments = listOf(navArgument("photoId") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val photoId = backStackEntry.arguments?.getString("photoId")
                val photos = viewModel.photos.collectAsState().value
                val image = photos.find { it.id == photoId }

                if (image != null) {
                    DetailsScreen(photo = image)
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Image not found")
                    }
                }
            }

        }
    }
}
