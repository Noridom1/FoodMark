package com.example.foodmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Route
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController
) {

}

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Places : BottomNavItem("foodStores", "Places", Icons.Default.Place)
    object Recipes : BottomNavItem("recipe", "Recipes", Icons.Default.Restaurant)
    object Menu : BottomNavItem("menu", "Menu", Icons.Default.Menu)
    object Tour : BottomNavItem("food_tour", "Tour", Icons.Default.Route)

}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Places,
    BottomNavItem.Recipes,
    BottomNavItem.Tour,
    BottomNavItem.Menu
)



