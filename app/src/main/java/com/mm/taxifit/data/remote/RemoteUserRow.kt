package com.mm.taxifit.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUserRow(
    @SerialName("id") val id: String
)

@Serializable
data class RemoteUserProfileRow(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("dni") val dni: String,
    @SerialName("role") val role: String,
    @SerialName("fullname") val fullName: String,
    @SerialName("phone") val phone: String
)

@Serializable
data class RemoteUserInsert(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("dni") val dni: String,
    @SerialName("role") val role: String,
    @SerialName("fullname") val fullname: String,
    @SerialName("phone") val phone: String
)

@Serializable
data class RemoteOwnerInsert(
    @SerialName("user_id") val userId: String,
    @SerialName("work_license") val workLicense: String
)

@Serializable
data class RemoteOwnerRow(
    @SerialName("user_id") val userId: String,
    @SerialName("work_license") val workLicense: String
)

@Serializable
data class RemoteDriverInsert(
    @SerialName("user_id") val userId: String,
    @SerialName("owner_id") val ownerId: String? = null
)

@Serializable
data class RemoteDriverRow(
    @SerialName("user_id") val userId: String,
    @SerialName("owner_id") val ownerId: String? = null
)

@Serializable
data class RemoteUserUpdate(
    @SerialName("email") val email: String,
    @SerialName("dni") val dni: String,
    @SerialName("fullname") val fullName: String,
    @SerialName("phone") val phone: String
)

@Serializable
data class RemoteOwnerUpdate(
    @SerialName("work_license") val workLicense: String
)

@Serializable
data class RemoteDriverUpdate(
    @SerialName("owner_id") val ownerId: String
)

@Serializable
data class RemoteUserRoleUpdate(
    @SerialName("role") val role: String
)
