package com.mm.taxifit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.repository.RepositoryProvider
import com.mm.taxifit.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = RepositoryProvider.authRepository

    private val _loginState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val loginState: StateFlow<UiState<String>> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Email y password son obligatorios")
            return
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            val success = repository.login(email.trim(), password)
            _loginState.value = if (success) {
                UiState.Success(email.trim())
            } else {
                UiState.Error("No se pudo iniciar sesion")
            }
        }
    }

    fun resetState() {
        _loginState.value = UiState.Empty
    }
}
