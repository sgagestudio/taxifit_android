package com.mm.taxifit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_user")
data class LocalUserEntity(
    @PrimaryKey val id: String,
    val email: String
)
