package com.mm.taxifit.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.mm.taxifit.BuildConfig
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.event.AuthEvent
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val supabase = SupabaseProvider.client

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeSessionStatus()
        observeAuthEvents()
        checkSession()
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetSignInWithGoogleOption.Builder(
                    BuildConfig.TAXIFIT_GOOGLE_WEB_CLIENT_ID
                ).build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    supabase.auth.signInWith(IDToken) {
                        idToken = googleIdToken
                        provider = Google
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "No se pudo obtener el token de Google")
                    }
                }
            } catch (ex: Exception) {
                if (ex is GetCredentialException) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = ex.message ?: "Login cancelado")
                    }
                    return@launch
                }
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = ex.message ?: "Login error")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
            } catch (ex: Exception) {
                _uiState.update {
                    it.copy(errorMessage = ex.message ?: "Sign out error")
                }
            }
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            val session = supabase.auth.currentSessionOrNull()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isAuthenticated = session != null
                )
            }
        }
    }

    private fun observeSessionStatus() {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                when (status) {
                    SessionStatus.Initializing -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is SessionStatus.Authenticated -> {
                        _uiState.update { it.copy(isLoading = false, isAuthenticated = true, errorMessage = null) }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _uiState.update { it.copy(isLoading = false, isAuthenticated = false, errorMessage = null) }
                    }
                    is SessionStatus.RefreshFailure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isAuthenticated = false,
                                errorMessage = "Session expired and could not be refreshed"
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeAuthEvents() {
        viewModelScope.launch {
            supabase.auth.events.collect { event ->
                when (event) {
                    is AuthEvent.OtpError -> {
                        _uiState.update {
                            it.copy(errorMessage = "OAuth error: $event")
                        }
                    }
                    is AuthEvent.RefreshFailure -> {
                        _uiState.update {
                            it.copy(errorMessage = "Refresh failed: $event")
                        }
                    }

                }
            }
        }
    }
}
