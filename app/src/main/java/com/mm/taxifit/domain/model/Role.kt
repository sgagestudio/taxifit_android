package com.mm.taxifit.domain.model

enum class Role(val dbValue: String) {
    DUENO("DUEÑO"),
    CONDUCTOR("CONDUCTOR"),
    DUENO_CONDUCTOR("DUEÑO_CONDUCTOR");

    companion object {
        fun fromDb(value: String?): Role? {
            return when (value) {
                DUENO.dbValue -> DUENO
                CONDUCTOR.dbValue -> CONDUCTOR
                DUENO_CONDUCTOR.dbValue -> DUENO_CONDUCTOR
                else -> null
            }
        }
    }
}
