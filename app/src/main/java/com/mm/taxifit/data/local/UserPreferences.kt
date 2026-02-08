package com.mm.taxifit.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mm.taxifit.domain.model.Role
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    private val lastRoleKey = stringPreferencesKey("last_role")

    suspend fun saveLastRole(role: Role) {
        context.dataStore.edit { prefs ->
            prefs[lastRoleKey] = role.dbValue
        }
    }

    suspend fun loadLastRole(): Role? {
        val value = context.dataStore.data.first()[lastRoleKey]
        return Role.fromDb(value)
    }

    suspend fun clearLastRole() {
        context.dataStore.edit { prefs ->
            prefs.remove(lastRoleKey)
        }
    }
}
