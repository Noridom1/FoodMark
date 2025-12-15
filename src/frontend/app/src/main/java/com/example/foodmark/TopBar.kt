package com.example.foodmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import kotlin.collections.forEach
import kotlin.sequences.any
import kotlin.text.orEmpty

@Immutable
data class TopBarConfig(
    val visible: Boolean = false,
    val title: String? = null,
    val navigationIcon: (@Composable () -> Unit)? = null,
    val actions: @Composable RowScope.() -> Unit = {},
)

class TopBarState {
    private var owner: Any? by mutableStateOf(null)

    var config by mutableStateOf(TopBarConfig())
        private set

    fun set(owner: Any, newConfig: TopBarConfig) {
        this.owner = owner
        this.config = newConfig
    }

    fun clear(owner: Any) {
        if (this.owner === owner) {
            this.owner = null
            this.config = TopBarConfig()
        }
    }
}

@Composable
fun rememberTopBarState(): TopBarState = remember { TopBarState() }

val LocalTopBarController = staticCompositionLocalOf<TopBarState> {
    error("TopBarController not provided")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarHost(state: TopBarState) {
    val cfg = state.config
    if (!cfg.visible) return

    CenterAlignedTopAppBar(
        title = {
            Text(
                cfg.title.orEmpty(),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = { cfg.navigationIcon?.invoke() },
        actions = cfg.actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            MaterialTheme.colorScheme.primary
        )
    )
}
@Composable
fun BottomBar(
    navController: NavController,
    currentDestination: NavDestination?
) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.primaryContainer
    val containerColor = MaterialTheme.colorScheme.secondary // or any Color

    Box(
        Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                clip = false
            )
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
//            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
//            NavigationBar(
//                containerColor = containerColor
//            ) {
//                bottomNavItems.forEach { item ->
//                    val selected = currentDestination
//                        ?.hierarchy
//                        ?.any { it.route == item.route } == true
//
//                    NavigationBarItem(
//                        selected = selected,
//                        onClick = {
//                            navController.navigate(item.route) {
//                                NavOptionsBuilder.popUpTo(BottomNavItem.Home.route) {
//                                    PopUpToBuilder.saveState = true
//                                }
//                                NavOptionsBuilder.launchSingleTop = true
//                                NavOptionsBuilder.restoreState = true
//                            }
//                        },
//                        icon = { Icon(item.icon, contentDescription = item.label) },
//                        label = { Text(item.label) },
//                        colors = NavigationBarItemDefaults.colors(
//                            selectedIconColor = selectedColor,
//                            selectedTextColor = selectedColor,
//                            unselectedIconColor = unselectedColor,
//                            unselectedTextColor = unselectedColor,
//                            indicatorColor = selectedColor.copy(alpha = 0.12f), // the pill behind selected item
//                            disabledIconColor = unselectedColor.copy(alpha = 0.38f),
//                            disabledTextColor = unselectedColor.copy(alpha = 0.38f)
//                        )
//                    )
//                }
//            }
        }
    }
}