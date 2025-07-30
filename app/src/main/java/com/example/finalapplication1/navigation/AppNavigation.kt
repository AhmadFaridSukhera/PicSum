package com.example.finalapplication1.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search

import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    object Favorites : BottomNavItem("favorites", "Favorites", Icons.Filled.Favorite)
    object Search : BottomNavItem("search", "Search", Icons.Filled.Search)
}
