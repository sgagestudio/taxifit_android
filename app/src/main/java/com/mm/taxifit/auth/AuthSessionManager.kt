package com.mm.taxifit.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.mm.taxifit.data.local.LocalUserStorage
import com.mm.taxifit.data.remote.RemoteUserRow

class AuthSessionManager(
    private val supabase: SupabaseClient,
    private val storage: SecureSessionStorage,
    private val localUserStorage: LocalUserStorage,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private var pendingVerificationEmail: String? = null

    init {
        observeSessionStatus()
        restoreSession()
    }

    fun signUp(email: String, password: String) {
        scope.launch {
            _state.update { AuthState.Loading }
            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                pendingVerificationEmail = email
                _state.update { AuthState.NeedsEmailVerification(email) }
            } catch (ex: Exception) {
                _state.update { AuthState.Error(ex.message ?: "Error registrando", email) }
            }
        }
    }

    fun signIn(email: String, password: String) {
        scope.launch {
            _state.update { AuthState.Loading }
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
            } catch (ex: Exception) {
                val message = ex.message ?: "Error de login"
                if (isEmailNotConfirmed(message)) {
                    pendingVerificationEmail = email
                    _state.update { AuthState.NeedsEmailVerification(email) }
                } else {
                    _state.update { AuthState.Error(message, email) }
                }
            }
        }
    }

    fun resendVerification(email: String) {
        scope.launch {
            try {
                supabase.auth.resendEmail(OtpType.Email.SIGNUP, email)
                pendingVerificationEmail = email
                _state.update { AuthState.NeedsEmailVerification(email) }
            } catch (ex: Exception) {
                _state.update {
                    AuthState.Error(ex.message ?: "No se pudo reenviar el correo", email, canResend = true)
                }
            }
        }
    }

    fun signOut() {
        scope.launch {
            try {
                supabase.auth.signOut()
            } catch (_: Exception) {
            } finally {
                withContext(Dispatchers.IO) {
                    storage.clear()
                    localUserStorage.clear()
                }
                pendingVerificationEmail = null
                _state.update { AuthState.LoggedOut }
            }
        }
    }

    fun backToLogin() {
        pendingVerificationEmail = null
        _state.update { AuthState.LoggedOut }
    }

    private fun restoreSession() {
        scope.launch {
            _state.update { AuthState.Loading }
            val session = withContext(Dispatchers.IO) { storage.load() }
            if (session == null) {
                _state.update { AuthState.LoggedOut }
                return@launch
            }
            try {
                supabase.auth.importSession(session, autoRefresh = true)
            } catch (_: Exception) {
                withContext(Dispatchers.IO) {
                    storage.clear()
                    localUserStorage.clear()
                }
                _state.update { AuthState.LoggedOut }
            }
        }
    }

    private fun observeSessionStatus() {
        scope.launch {
            supabase.auth.sessionStatus.collect { status ->
                when (status) {
                    SessionStatus.Initializing -> {
                        _state.update { AuthState.Loading }
                    }
                    is SessionStatus.Authenticated -> {
                        pendingVerificationEmail = null
                        saveSession(status.session)
                        saveLocalUser(status.session)
                        val requiresOnboarding = needsOnboarding(status.session)
                        if (requiresOnboarding) {
                            _state.update { AuthState.NeedsOnboarding(status.session.user?.email) }
                        } else {
                            _state.update { AuthState.LoggedIn(status.session.user?.email) }
                        }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        val pending = pendingVerificationEmail
                        if (pending != null) {
                            _state.update { AuthState.NeedsEmailVerification(pending) }
                        } else {
                            _state.update { AuthState.LoggedOut }
                        }
                    }
                    is SessionStatus.RefreshFailure -> {
                        withContext(Dispatchers.IO) {
                            storage.clear()
                            localUserStorage.clear()
                        }
                        _state.update { AuthState.Error("Sesion expirada. Inicia sesion de nuevo.") }
                    }
                }
            }
        }
    }

    private suspend fun saveSession(session: UserSession) {
        withContext(Dispatchers.IO) {
            storage.save(session)
        }
    }

    private suspend fun saveLocalUser(session: UserSession) {
        val user = session.user ?: return
        val email = user.email ?: return
        withContext(Dispatchers.IO) {
            localUserStorage.save(id = user.id, email = email)
        }
    }

    private suspend fun needsOnboarding(session: UserSession): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val localUser = localUserStorage.load() ?: return@withContext false
                val userId = localUser.id
                val rows = supabase.postgrest["users"]
                    .select {
                        filter { eq("id", userId) }
                        limit(1)
                    }
                    .decodeList<RemoteUserRow>()
                rows.isEmpty()
            } catch (_: Exception) {
                false
            }
        }
    }

    private fun isEmailNotConfirmed(message: String): Boolean {
        val normalized = message.lowercase()
        return normalized.contains("confirm") && normalized.contains("email") ||
            normalized.contains("not confirmed") ||
            normalized.contains("email_not_confirmed") ||
            normalized.contains("email not confirmed")
    }
}
