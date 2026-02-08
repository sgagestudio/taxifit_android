package com.mm.taxifit.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mm.taxifit.data.repository.AppUserRole
import com.mm.taxifit.ui.screens.common.ProfileSettingsScreen
import com.mm.taxifit.ui.screens.common.SettingsScreen
import com.mm.taxifit.ui.screens.driver.GoalsScreen
import com.mm.taxifit.ui.screens.driver.HistoryScreen
import com.mm.taxifit.ui.screens.driver.HomeDriverScreen
import com.mm.taxifit.ui.screens.driver.VehicleStatusScreen
import com.mm.taxifit.ui.screens.owner.FinancialsScreen
import com.mm.taxifit.ui.screens.owner.FleetManagementScreen
import com.mm.taxifit.ui.screens.owner.HomeOwnerScreen
import com.mm.taxifit.ui.screens.owner.ReportsScreen

@Composable
fun RoleHomeNavHost(role: AppUserRole) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val startRoute = if (role == AppUserRole.OWNER) AppRoute.OwnerHome.route else AppRoute.DriverHome.route
    val destinations = if (role == AppUserRole.OWNER) {
        RoleNavigation.ownerBottomDestinations
    } else {
        RoleNavigation.driverBottomDestinations
    }
    val topLevelRoutes = destinations.map { it.route }.toSet()

    Scaffold(
        bottomBar = {
            if (currentRoute in topLevelRoutes) {
                NavigationBar {
                    destinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(startRoute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(destination.label.take(1)) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(paddingValues)
        ) {
            if (role == AppUserRole.OWNER) {
                composable(AppRoute.OwnerHome.route) { HomeOwnerScreen() }
                composable(AppRoute.OwnerFleet.route) { FleetManagementScreen() }
                composable(AppRoute.OwnerFinancials.route) { FinancialsScreen() }
                composable(AppRoute.OwnerReports.route) { ReportsScreen() }
                composable(AppRoute.OwnerSettings.route) {
                    SettingsScreen(
                        currentViewRole = AppUserRole.OWNER,
                        onOpenProfile = { navController.navigate(AppRoute.OwnerProfileSettings.route) }
                    )
                }
                composable(AppRoute.OwnerProfileSettings.route) {
                    ProfileSettingsScreen(
                        currentViewRole = AppUserRole.OWNER,
                        onBack = { navController.popBackStack() }
                    )
                }
            } else {
                composable(AppRoute.DriverHome.route) { HomeDriverScreen() }
                composable(AppRoute.DriverHistory.route) { HistoryScreen() }
                composable(AppRoute.DriverGoals.route) { GoalsScreen() }
                composable(AppRoute.DriverVehicleStatus.route) { VehicleStatusScreen() }
                composable(AppRoute.DriverSettings.route) {
                    SettingsScreen(
                        currentViewRole = AppUserRole.DRIVER,
                        onOpenProfile = { navController.navigate(AppRoute.DriverProfileSettings.route) }
                    )
                }
                composable(AppRoute.DriverProfileSettings.route) {
                    ProfileSettingsScreen(
                        currentViewRole = AppUserRole.DRIVER,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
