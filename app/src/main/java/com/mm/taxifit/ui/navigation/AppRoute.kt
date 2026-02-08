package com.mm.taxifit.ui.navigation

import com.mm.taxifit.data.repository.AppUserRole

sealed class AppRoute(val route: String) {
    data object Login : AppRoute("login")
    data object PersonalData : AppRoute("personal_data")
    data object RoleSelection : AppRoute("role_selection")
    data object VehicleData : AppRoute("vehicle_data")

    data object DriverHome : AppRoute("driver_home")
    data object DriverHistory : AppRoute("driver_history")
    data object DriverGoals : AppRoute("driver_goals")
    data object DriverVehicleStatus : AppRoute("driver_vehicle_status")
    data object DriverSettings : AppRoute("driver_settings")
    data object DriverProfileSettings : AppRoute("driver_profile_settings")

    data object OwnerHome : AppRoute("owner_home")
    data object OwnerFleet : AppRoute("owner_fleet")
    data object OwnerFinancials : AppRoute("owner_financials")
    data object OwnerReports : AppRoute("owner_reports")
    data object OwnerSettings : AppRoute("owner_settings")
    data object OwnerProfileSettings : AppRoute("owner_profile_settings")
}

data class BottomDestination(
    val route: String,
    val label: String
)

object RoleNavigation {
    val driverBottomDestinations = listOf(
        BottomDestination(AppRoute.DriverHome.route, "Inicio"),
        BottomDestination(AppRoute.DriverHistory.route, "Historial"),
        BottomDestination(AppRoute.DriverGoals.route, "Objetivos"),
        BottomDestination(AppRoute.DriverVehicleStatus.route, "Taxi"),
        BottomDestination(AppRoute.DriverSettings.route, "Ajustes")
    )

    val ownerBottomDestinations = listOf(
        BottomDestination(AppRoute.OwnerHome.route, "Inicio"),
        BottomDestination(AppRoute.OwnerFleet.route, "Flota"),
        BottomDestination(AppRoute.OwnerFinancials.route, "Finanzas"),
        BottomDestination(AppRoute.OwnerReports.route, "Reportes"),
        BottomDestination(AppRoute.OwnerSettings.route, "Ajustes")
    )

    val driverRoutes = setOf(
        AppRoute.DriverHome.route,
        AppRoute.DriverHistory.route,
        AppRoute.DriverGoals.route,
        AppRoute.DriverVehicleStatus.route,
        AppRoute.DriverSettings.route,
        AppRoute.DriverProfileSettings.route
    )

    val ownerRoutes = setOf(
        AppRoute.OwnerHome.route,
        AppRoute.OwnerFleet.route,
        AppRoute.OwnerFinancials.route,
        AppRoute.OwnerReports.route,
        AppRoute.OwnerSettings.route,
        AppRoute.OwnerProfileSettings.route
    )

    fun roleForRoute(route: String?): AppUserRole? {
        return when {
            route == null -> null
            driverRoutes.contains(route) -> AppUserRole.DRIVER
            ownerRoutes.contains(route) -> AppUserRole.OWNER
            else -> null
        }
    }
}
