package com.mm.taxifit.data.repository

import kotlinx.coroutines.delay

private object MockSessionStore {
    var personalData: PersonalData? = null
    var role: AppUserRole? = null
    var vehicleData: VehicleData? = null
}

class MockAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): Boolean {
        delay(250)
        return email.isNotBlank() && password.isNotBlank()
    }
}

class MockOnboardingRepository : OnboardingRepository {
    override suspend fun savePersonalData(data: PersonalData) {
        delay(150)
        MockSessionStore.personalData = data
    }

    override suspend fun saveRole(role: AppUserRole) {
        delay(120)
        MockSessionStore.role = role
    }

    override suspend fun saveVehicleData(data: VehicleData) {
        delay(150)
        MockSessionStore.vehicleData = data
    }

    override suspend fun getSavedRole(): AppUserRole? {
        delay(100)
        return MockSessionStore.role
    }
}

class MockDriverRepository : DriverRepository {
    override suspend fun getHomeStatus(): DriverHomeStatus {
        delay(200)
        return DriverHomeStatus(
            shiftState = "Jornada activa",
            elapsed = "01:42:18",
            mainAction = "Iniciar carrera"
        )
    }

    override suspend fun getHistory(): List<TripRecord> {
        delay(220)
        return listOf(
            TripRecord(
                id = "T-1092",
                date = "2026-02-05 20:10",
                amount = "EUR 18.40",
                duration = "21 min"
            ),
            TripRecord(
                id = "T-1091",
                date = "2026-02-05 18:42",
                amount = "EUR 9.80",
                duration = "11 min"
            ),
            TripRecord(
                id = "T-1090",
                date = "2026-02-05 17:23",
                amount = "EUR 14.20",
                duration = "16 min"
            )
        )
    }

    override suspend fun getGoals(): List<GoalProgress> {
        delay(180)
        return listOf(
            GoalProgress(label = "Lun", income = 120, target = 150),
            GoalProgress(label = "Mar", income = 146, target = 150),
            GoalProgress(label = "Mie", income = 160, target = 150),
            GoalProgress(label = "Jue", income = 90, target = 150),
            GoalProgress(label = "Vie", income = 175, target = 150)
        )
    }

    override suspend fun getVehicleStatus(): VehicleStatus {
        delay(180)
        return VehicleStatus(
            assignedTaxi = "Licencia 123 - Toyota Corolla",
            kilometers = "128,430 km",
            nextInspection = "Revision: 2026-03-14"
        )
    }
}

class MockOwnerRepository : OwnerRepository {
    override suspend fun getKpis(): List<OwnerKpi> {
        delay(220)
        return listOf(
            OwnerKpi(
                title = "Recaudacion Total",
                value = "EUR 12,940",
                hint = "+8.4% vs mes anterior"
            ),
            OwnerKpi(
                title = "Conductores Activos",
                value = "9",
                hint = "2 en descanso"
            ),
            OwnerKpi(
                title = "Alertas",
                value = "3",
                hint = "1 revision pendiente"
            )
        )
    }

    override suspend fun getFleetAssignments(): List<FleetAssignment> {
        delay(180)
        return listOf(
            FleetAssignment("Licencia 123 -> Juan Perez"),
            FleetAssignment("Licencia 278 -> Laura Diaz"),
            FleetAssignment("Licencia 411 -> Marco Neri")
        )
    }

    override suspend fun getExpenses(): List<ExpenseItem> {
        delay(210)
        return listOf(
            ExpenseItem(
                id = "G-19",
                concept = "Gasoil - Estacion Norte",
                amount = "EUR 63.90",
                receiptPreview = "ticket_gasoil_19.jpg",
                isValidated = false
            ),
            ExpenseItem(
                id = "M-07",
                concept = "Mantenimiento frenos",
                amount = "EUR 240.00",
                receiptPreview = "ticket_taller_07.jpg",
                isValidated = true
            )
        )
    }

    override suspend fun getReportMonths(): List<ReportMonth> {
        delay(120)
        return listOf(
            ReportMonth(key = "2026-01", title = "Enero 2026"),
            ReportMonth(key = "2025-12", title = "Diciembre 2025"),
            ReportMonth(key = "2025-11", title = "Noviembre 2025")
        )
    }

    override suspend fun exportReport(month: ReportMonth): Boolean {
        delay(200)
        return month.key.isNotBlank()
    }
}
