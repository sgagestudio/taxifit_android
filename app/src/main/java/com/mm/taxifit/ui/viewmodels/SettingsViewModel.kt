package com.mm.taxifit.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.repository.EditableProfile
import com.mm.taxifit.data.repository.SupabaseProfileSettingsRepository
import com.mm.taxifit.domain.model.Role
import com.mm.taxifit.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SupabaseProfileSettingsRepository(application)

    private val _profileState = MutableStateFlow<UiState<EditableProfile>>(UiState.Loading)
    val profileState: StateFlow<UiState<EditableProfile>> = _profileState.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val saveState: StateFlow<UiState<String>> = _saveState.asStateFlow()

    private val _signOutState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val signOutState: StateFlow<UiState<String>> = _signOutState.asStateFlow()

    private val _viewChangeState = MutableStateFlow<UiState<Role>>(UiState.Empty)
    val viewChangeState: StateFlow<UiState<Role>> = _viewChangeState.asStateFlow()

    private val _upgradeState = MutableStateFlow<UiState<EditableProfile>>(UiState.Empty)
    val upgradeState: StateFlow<UiState<EditableProfile>> = _upgradeState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            try {
                val profile = repository.loadProfile()
                _profileState.value = UiState.Success(profile)
            } catch (ex: Exception) {
                _profileState.value = UiState.Error(ex.message ?: "No se pudo cargar el perfil")
            }
        }
    }

    fun saveProfile(
        current: EditableProfile,
        email: String,
        dni: String,
        fullName: String,
        phone: String,
        workLicense: String,
        validateWorkLicense: Boolean
    ) {
        if (email.isBlank() || dni.isBlank() || fullName.isBlank() || phone.isBlank()) {
            _saveState.value = UiState.Error("Completa correo, DNI, nombre completo y telefono")
            return
        }
        if (validateWorkLicense && workLicense.isBlank()) {
            _saveState.value = UiState.Error("Completa el numero licencia de taxi")
            return
        }

        val updatedProfile = current.copy(
            email = email.trim(),
            dni = dni.trim(),
            fullName = fullName.trim(),
            phone = phone.trim(),
            workLicense = if (validateWorkLicense) workLicense.trim() else current.workLicense
        )

        viewModelScope.launch {
            _saveState.value = UiState.Loading
            try {
                repository.saveProfile(updatedProfile)
                _profileState.value = UiState.Success(updatedProfile)
                _saveState.value = UiState.Success("Perfil actualizado")
            } catch (ex: Exception) {
                _saveState.value = UiState.Error(ex.message ?: "No se pudo guardar el perfil")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _signOutState.value = UiState.Loading
            try {
                repository.signOut()
                _signOutState.value = UiState.Success("Sesion cerrada")
            } catch (ex: Exception) {
                _signOutState.value = UiState.Error(ex.message ?: "No se pudo cerrar sesion")
            }
        }
    }

    fun switchViewTo(targetRole: Role) {
        viewModelScope.launch {
            _viewChangeState.value = UiState.Loading
            try {
                repository.saveLastRole(targetRole)
                _viewChangeState.value = UiState.Success(targetRole)
            } catch (ex: Exception) {
                _viewChangeState.value = UiState.Error(ex.message ?: "No se pudo cambiar la vista")
            }
        }
    }

    fun promoteConductorToHybrid(workLicense: String) {
        if (workLicense.isBlank()) {
            _upgradeState.value = UiState.Error("Completa el numero licencia de taxi")
            return
        }
        viewModelScope.launch {
            _upgradeState.value = UiState.Loading
            try {
                val updated = repository.promoteConductorToHybrid(workLicense)
                _profileState.value = UiState.Success(updated)
                _upgradeState.value = UiState.Success(updated)
            } catch (ex: Exception) {
                _upgradeState.value = UiState.Error(ex.message ?: "No se pudo activar el perfil dual")
            }
        }
    }

    fun promoteOwnerToHybrid() {
        viewModelScope.launch {
            _upgradeState.value = UiState.Loading
            try {
                val updated = repository.promoteOwnerToHybrid()
                _profileState.value = UiState.Success(updated)
                _upgradeState.value = UiState.Success(updated)
            } catch (ex: Exception) {
                _upgradeState.value = UiState.Error(ex.message ?: "No se pudo activar el perfil dual")
            }
        }
    }

    fun clearSaveState() {
        _saveState.value = UiState.Empty
    }

    fun clearSignOutState() {
        _signOutState.value = UiState.Empty
    }

    fun clearViewChangeState() {
        _viewChangeState.value = UiState.Empty
    }

    fun clearUpgradeState() {
        _upgradeState.value = UiState.Empty
    }
}
