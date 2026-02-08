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
import com.mm.taxifit.ui.screens.driver.GoalsScreen
import com.mm.taxifit.ui.screens.driver.HistoryScreen
import com.mm.taxifit.ui.screens.driver.HomeDriverScreen
import com.mm.taxifit.ui.screens.driver.VehicleStatusScreen
import com.mm.taxifit.ui.screens.onboarding.LoginScreen
import com.mm.taxifit.ui.screens.onboarding.PersonalDataScreen
import com.mm.taxifit.ui.screens.onboarding.RoleSelectionScreen
import com.mm.taxifit.ui.screens.onboarding.VehicleDataScreen
import com.mm.taxifit.ui.screens.owner.FinancialsScreen
import com.mm.taxifit.ui.screens.owner.FleetManagementScreen
import com.mm.taxifit.ui.screens.owner.HomeOwnerScreen
import com.mm.taxifit.ui.screens.owner.ReportsScreen

@Composable
fun TaxiNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val activeRole = RoleNavigation.roleForRoute(currentRoute)
    val bottomDestinations = when (activeRole) {
        AppUserRole.DRIVER -> RoleNavigation.driverBottomDestinations
        AppUserRole.OWNER -> RoleNavigation.ownerBottomDestinations
        null -> emptyList()
    }

    Scaffold(
        bottomBar = {
            if (bottomDestinations.isNotEmpty()) {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        val selected = currentRoute == destination.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                val startRoute = when (activeRole) {
                                    AppUserRole.DRIVER -> AppRoute.DriverHome.route
                                    AppUserRole.OWNER -> AppRoute.OwnerHome.route
                                    null -> AppRoute.Login.route
                                }
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
            startDestination = AppRoute.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppRoute.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(AppRoute.PersonalData.route) {
                            popUpTo(AppRoute.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoute.PersonalData.route) {
                PersonalDataScreen(
                    onContinue = { navController.navigate(AppRoute.RoleSelection.route) }
                )
            }

            composable(AppRoute.RoleSelection.route) {
                RoleSelectionScreen(
                    onRoleSelected = { role ->
                        if (role == AppUserRole.OWNER) {
                            navController.navigate(AppRoute.VehicleData.route)
                        } else {
                            navController.navigate(AppRoute.DriverHome.route) {
                                popUpTo(AppRoute.Login.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(AppRoute.VehicleData.route) {
                VehicleDataScreen(
                    onContinue = {
                        navController.navigate(AppRoute.OwnerHome.route) {
                            popUpTo(AppRoute.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoute.DriverHome.route) { HomeDriverScreen() }
            composable(AppRoute.DriverHistory.route) { HistoryScreen() }
            composable(AppRoute.DriverGoals.route) { GoalsScreen() }
            composable(AppRoute.DriverVehicleStatus.route) { VehicleStatusScreen() }

            composable(AppRoute.OwnerHome.route) { HomeOwnerScreen() }
            composable(AppRoute.OwnerFleet.route) { FleetManagementScreen() }
            composable(AppRoute.OwnerFinancials.route) { FinancialsScreen() }
            composable(AppRoute.OwnerReports.route) { ReportsScreen() }
        }
    }
}
