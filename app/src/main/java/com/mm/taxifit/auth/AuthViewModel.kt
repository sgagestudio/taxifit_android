package com.mm.taxifit.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.local.LocalUserStorage
import kotlinx.coroutines.flow.StateFlow

sealed class AuthState {
    data object Loading : AuthState()
    data object LoggedOut : AuthState()
    data class NeedsEmailVerification(val email: String) : AuthState()
    data class LoggedIn(val email: String?) : AuthState()
    data class Error(val message: String, val email: String? = null, val canResend: Boolean = false) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = AuthSessionManager(
        supabase = SupabaseProvider.client,
        storage = SecureSessionStorage(application),
        localUserStorage = LocalUserStorage(application),
        scope = viewModelScope
    )

    val state: StateFlow<AuthState> = sessionManager.state

    fun signUp(email: String, password: String) = sessionManager.signUp(email, password)

    fun signIn(email: String, password: String) = sessionManager.signIn(email, password)

    fun resendVerification(email: String) = sessionManager.resendVerification(email)

    fun signOut() = sessionManager.signOut()

    fun backToLogin() = sessionManager.backToLogin()
}
