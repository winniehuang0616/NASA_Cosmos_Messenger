package com.example.nasacosmosmessengerapp.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nasacosmosmessengerapp.presentation.favorites.FavoritesScreen
import com.example.nasacosmosmessengerapp.presentation.nova.NovaScreen
import com.example.nasacosmosmessengerapp.presentation.theme.NASACosmosMessengerAPPTheme

object MainDestinations {
    const val Nova = "nova"
    const val Favorites = "favorites"
}

@Composable
fun MainScaffold(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == MainDestinations.Nova,
                    onClick = {
                        navController.navigate(MainDestinations.Nova) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Filled.RocketLaunch, contentDescription = null) },
                    label = { Text("Nova") }
                )
                NavigationBarItem(
                    selected = currentRoute == MainDestinations.Favorites,
                    onClick = {
                        navController.navigate(MainDestinations.Favorites) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                    label = { Text("收藏") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainDestinations.Nova,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainDestinations.Nova) { NovaScreen() }
            composable(MainDestinations.Favorites) { FavoritesScreen() }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScaffoldPreview() {
    NASACosmosMessengerAPPTheme {
        MainScaffold()
    }
}
