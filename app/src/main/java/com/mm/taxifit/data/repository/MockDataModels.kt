package com.mm.taxifit.data.repository

data class PersonalData(
    val fullName: String = "",
    val dni: String = "",
    val phone: String = ""
)

data class VehicleData(
    val plate: String = "",
    val licenseNumber: String = "",
    val taximeterModel: String = ""
)

data class DriverHomeStatus(
    val shiftState: String,
    val elapsed: String,
    val mainAction: String
)

data class TripRecord(
    val id: String,
    val date: String,
    val amount: String,
    val duration: String
)

data class GoalProgress(
    val label: String,
    val income: Int,
    val target: Int
)

data class VehicleStatus(
    val assignedTaxi: String,
    val kilometers: String,
    val nextInspection: String
)

data class OwnerKpi(
    val title: String,
    val value: String,
    val hint: String
)

data class FleetAssignment(
    val label: String
)

data class ExpenseItem(
    val id: String,
    val concept: String,
    val amount: String,
    val receiptPreview: String,
    val isValidated: Boolean
)

data class ReportMonth(
    val key: String,
    val title: String
)
