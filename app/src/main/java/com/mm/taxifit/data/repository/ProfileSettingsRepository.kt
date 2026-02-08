package com.mm.taxifit.data.repository

import android.content.Context
import com.mm.taxifit.auth.SecureSessionStorage
import com.mm.taxifit.auth.SupabaseProvider
import com.mm.taxifit.data.local.LocalUserStorage
import com.mm.taxifit.data.local.UserPreferences
import com.mm.taxifit.data.remote.RemoteOwnerInsert
import com.mm.taxifit.data.remote.RemoteOwnerRow
import com.mm.taxifit.data.remote.RemoteOwnerUpdate
import com.mm.taxifit.data.remote.RemoteDriverInsert
import com.mm.taxifit.data.remote.RemoteDriverRow
import com.mm.taxifit.data.remote.RemoteDriverUpdate
import com.mm.taxifit.data.remote.RemoteUserProfileRow
import com.mm.taxifit.data.remote.RemoteUserRoleUpdate
import com.mm.taxifit.data.remote.RemoteUserUpdate
import com.mm.taxifit.domain.model.Role
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest

data class EditableProfile(
    val userId: String,
    val role: Role,
    val email: String,
    val dni: String,
    val fullName: String,
    val phone: String,
    val workLicense: String?
) {
    fun requiresWorkLicense(): Boolean {
        return role == Role.DUENO || role == Role.DUENO_CONDUCTOR
    }
}

interface ProfileSettingsRepository {
    suspend fun loadProfile(): EditableProfile
    suspend fun saveProfile(profile: EditableProfile)
    suspend fun saveLastRole(role: Role)
    suspend fun promoteConductorToHybrid(workLicense: String): EditableProfile
    suspend fun promoteOwnerToHybrid(): EditableProfile
    suspend fun signOut()
}

class SupabaseProfileSettingsRepository(
    context: Context
) : ProfileSettingsRepository {
    private val appContext = context.applicationContext
    private val localUserStorage = LocalUserStorage(appContext)
    private val secureSessionStorage = SecureSessionStorage(appContext)
    private val userPreferences = UserPreferences(appContext)
    private val supabase = SupabaseProvider.client

    override suspend fun loadProfile(): EditableProfile {
        val localUser = localUserStorage.load()
            ?: throw IllegalStateException("No se encontro usuario local")

        val userRows = supabase.postgrest["users"]
            .select {
                filter { eq("id", localUser.id) }
                limit(1)
            }
            .decodeList<RemoteUserProfileRow>()

        val row = userRows.firstOrNull()
            ?: throw IllegalStateException("No se encontro perfil remoto")

        val role = Role.fromDb(row.role) ?: Role.CONDUCTOR
        val workLicense = if (role == Role.DUENO || role == Role.DUENO_CONDUCTOR) {
            val ownerRows = supabase.postgrest["owners"]
                .select {
                    filter { eq("user_id", localUser.id) }
                    limit(1)
                }
                .decodeList<RemoteOwnerRow>()
            ownerRows.firstOrNull()?.workLicense.orEmpty()
        } else {
            null
        }

        return EditableProfile(
            userId = row.id,
            role = role,
            email = row.email,
            dni = row.dni,
            fullName = row.fullName,
            phone = row.phone,
            workLicense = workLicense
        )
    }

    override suspend fun saveProfile(profile: EditableProfile) {
        supabase.postgrest["users"]
            .update(
                RemoteUserUpdate(
                    email = profile.email,
                    dni = profile.dni,
                    fullName = profile.fullName,
                    phone = profile.phone
                )
            ) {
                filter { eq("id", profile.userId) }
            }

        localUserStorage.save(id = profile.userId, email = profile.email)

        if (profile.requiresWorkLicense()) {
            val currentWorkLicense = profile.workLicense.orEmpty()
            val ownerRows = supabase.postgrest["owners"]
                .select {
                    filter { eq("user_id", profile.userId) }
                    limit(1)
                }
                .decodeList<RemoteOwnerRow>()

            if (ownerRows.isEmpty()) {
                supabase.postgrest["owners"].insert(
                    RemoteOwnerInsert(
                        userId = profile.userId,
                        workLicense = currentWorkLicense
                    )
                )
            } else {
                supabase.postgrest["owners"]
                    .update(RemoteOwnerUpdate(workLicense = currentWorkLicense)) {
                        filter { eq("user_id", profile.userId) }
                    }
            }
        }
    }

    override suspend fun saveLastRole(role: Role) {
        userPreferences.saveLastRole(role)
    }

    override suspend fun promoteConductorToHybrid(workLicense: String): EditableProfile {
        val current = loadProfile()
        if (current.role != Role.CONDUCTOR) {
            throw IllegalStateException("Este flujo solo aplica para conductores")
        }
        if (workLicense.isBlank()) {
            throw IllegalStateException("Completa el numero licencia de taxi")
        }

        supabase.postgrest["users"]
            .update(RemoteUserRoleUpdate(role = Role.DUENO_CONDUCTOR.dbValue)) {
                filter { eq("id", current.userId) }
            }

        val ownerRows = supabase.postgrest["owners"]
            .select {
                filter { eq("user_id", current.userId) }
                limit(1)
            }
            .decodeList<RemoteOwnerRow>()

        if (ownerRows.isEmpty()) {
            supabase.postgrest["owners"].insert(
                RemoteOwnerInsert(
                    userId = current.userId,
                    workLicense = workLicense.trim()
                )
            )
        } else {
            supabase.postgrest["owners"]
                .update(RemoteOwnerUpdate(workLicense = workLicense.trim())) {
                    filter { eq("user_id", current.userId) }
                }
        }

        ensureDriverWithOwner(userId = current.userId)

        return current.copy(
            role = Role.DUENO_CONDUCTOR,
            workLicense = workLicense.trim()
        )
    }

    override suspend fun promoteOwnerToHybrid(): EditableProfile {
        val current = loadProfile()
        if (current.role != Role.DUENO) {
            throw IllegalStateException("Este flujo solo aplica para dueños")
        }

        supabase.postgrest["users"]
            .update(RemoteUserRoleUpdate(role = Role.DUENO_CONDUCTOR.dbValue)) {
                filter { eq("id", current.userId) }
            }

        ensureDriverWithOwner(userId = current.userId)

        return current.copy(role = Role.DUENO_CONDUCTOR)
    }

    override suspend fun signOut() {
        try {
            supabase.auth.signOut()
        } finally {
            secureSessionStorage.clear()
            localUserStorage.clear()
            userPreferences.clearLastRole()
        }
    }

    private suspend fun ensureDriverWithOwner(userId: String) {
        val driverRows = supabase.postgrest["drivers"]
            .select {
                filter { eq("user_id", userId) }
                limit(1)
            }
            .decodeList<RemoteDriverRow>()

        if (driverRows.isEmpty()) {
            supabase.postgrest["drivers"].insert(
                RemoteDriverInsert(
                    userId = userId,
                    ownerId = userId
                )
            )
        } else {
            supabase.postgrest["drivers"]
                .update(RemoteDriverUpdate(ownerId = userId)) {
                    filter { eq("user_id", userId) }
                }
        }
    }
}
