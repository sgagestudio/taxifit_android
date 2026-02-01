package com.mm.taxifit.domain.model

data class Vehicle(
    val userId: String,
    val plate: String,
    val taximeterModel: String
) {
    fun isComplete(): Boolean = plate.isNotBlank() && taximeterModel.isNotBlank()
}
