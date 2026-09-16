package com.epaperlauncher.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.epaperlauncher.feature.launcherhome.presentation.HomeScreen
import com.epaperlauncher.feature.launcherdrawer.presentation.DrawerScreen
import com.epaperlauncher.feature.settings.presentation.components.SettingsScreen

sealed class NavRoute(val route: String) {
    object Home : NavRoute("home")
    object Drawer : NavRoute("drawer")
    object Settings : NavRoute("settings")
    object AppInfo : NavRoute("app_info/{packageName}") {
        fun createRoute(packageName: String) = "app_info/$packageName"
    }
}

@Composable
fun LauncherNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Home.route
    ) {
        composable(NavRoute.Home.route) {
            HomeScreen(
                onNavigateToDrawer = { navController.navigate(NavRoute.Drawer.route) },
                onNavigateToSettings = { navController.navigate(NavRoute.Settings.route) },
                onAppLaunch = { /* Handle app launch */ }
            )
        }
        composable(NavRoute.Drawer.route) {
            DrawerScreen(
                onNavigateBack = { navController.popBackStack() },
                onAppLaunch = { /* Handle app launch */ }
            )
        }
        composable(NavRoute.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
