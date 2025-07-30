package com.example.finalapplication1.navigation



import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalapplication1.PictureViewModel

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
            composable(BottomNavItem.Search.route) { SearchScreen(viewModel) }
            composable(BottomNavItem.Favorites.route) { FavoriteScreen(viewModel) }
        }
    }
}