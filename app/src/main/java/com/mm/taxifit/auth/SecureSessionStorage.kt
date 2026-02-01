package com.mm.taxifit.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.github.jan.supabase.auth.user.UserSession
import kotlin.math.max

class SecureSessionStorage(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun save(session: UserSession) {
        val expiresAtMillis = System.currentTimeMillis() + session.expiresIn * 1000L
        prefs.edit()
            .putString(KEY_ACCESS, session.accessToken)
            .putString(KEY_REFRESH, session.refreshToken)
            .putString(KEY_PROVIDER_TOKEN, session.providerToken)
            .putString(KEY_PROVIDER_REFRESH, session.providerRefreshToken)
            .putLong(KEY_EXPIRES_AT, expiresAtMillis)
            .putString(KEY_TOKEN_TYPE, session.tokenType)
            .putString(KEY_TYPE, session.type)
            .putString(KEY_USER_EMAIL, session.user?.email)
            .apply()
    }

    fun load(): UserSession? {
        val accessToken = prefs.getString(KEY_ACCESS, null) ?: return null
        val refreshToken = prefs.getString(KEY_REFRESH, null) ?: return null
        val expiresAtMillis = prefs.getLong(KEY_EXPIRES_AT, 0L)
        val tokenType = prefs.getString(KEY_TOKEN_TYPE, null) ?: "bearer"
        val providerToken = prefs.getString(KEY_PROVIDER_TOKEN, null)
        val providerRefreshToken = prefs.getString(KEY_PROVIDER_REFRESH, null)
        val type = prefs.getString(KEY_TYPE, "") ?: ""

        val remainingSeconds = max(0L, (expiresAtMillis - System.currentTimeMillis()) / 1000L)

        return UserSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            providerRefreshToken = providerRefreshToken,
            providerToken = providerToken,
            expiresIn = remainingSeconds,
            tokenType = tokenType,
            user = null,
            type = type
        )
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "taxifit_secure_session"
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_PROVIDER_TOKEN = "provider_token"
        private const val KEY_PROVIDER_REFRESH = "provider_refresh_token"
        private const val KEY_EXPIRES_AT = "expires_at"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_TYPE = "type"
        private const val KEY_USER_EMAIL = "user_email"
    }
}
