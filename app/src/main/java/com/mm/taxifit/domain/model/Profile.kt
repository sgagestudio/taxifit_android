package com.mm.taxifit.domain.model

data class Profile(
    val userId: String,
    val name: String,
    val dni: String,
    val phone: String,
    val licenseNumber: String
) {
    fun isComplete(): Boolean {
        return name.isNotBlank() &&
            dni.isNotBlank() &&
            phone.isNotBlank() &&
            licenseNumber.isNotBlank()
    }
}
