package com.example.finalapplication1.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Favorites
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val shouldShowBottomBar = items.any { it.route == currentRoute }

    if (shouldShowBottomBar) {
    NavigationBar(
        modifier = Modifier.height(90.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                alwaysShowLabel = true,
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {
                    Text(
                        item.label,
                        color = if (currentRoute == item.route)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true

                        }
                    }
                }
            )
        }
    }
}
}
