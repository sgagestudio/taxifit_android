package com.mm.taxifit.data.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
}

interface OnboardingRepository {
    suspend fun savePersonalData(data: PersonalData)
    suspend fun saveRole(role: AppUserRole)
    suspend fun saveVehicleData(data: VehicleData)
    suspend fun getSavedRole(): AppUserRole?
}

interface DriverRepository {
    suspend fun getHomeStatus(): DriverHomeStatus
    suspend fun getHistory(): List<TripRecord>
    suspend fun getGoals(): List<GoalProgress>
    suspend fun getVehicleStatus(): VehicleStatus
}

interface OwnerRepository {
    suspend fun getKpis(): List<OwnerKpi>
    suspend fun getFleetAssignments(): List<FleetAssignment>
    suspend fun getExpenses(): List<ExpenseItem>
    suspend fun getReportMonths(): List<ReportMonth>
    suspend fun exportReport(month: ReportMonth): Boolean
}
