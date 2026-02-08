package com.mm.taxifit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.repository.AppUserRole
import com.mm.taxifit.data.repository.RepositoryProvider
import com.mm.taxifit.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoleSelectionViewModel : ViewModel() {
    private val repository = RepositoryProvider.onboardingRepository

    private val _rolesState = MutableStateFlow<UiState<List<AppUserRole>>>(UiState.Loading)
    val rolesState: StateFlow<UiState<List<AppUserRole>>> = _rolesState.asStateFlow()

    private val _selectionState = MutableStateFlow<UiState<AppUserRole>>(UiState.Empty)
    val selectionState: StateFlow<UiState<AppUserRole>> = _selectionState.asStateFlow()

    init {
        loadRoles()
    }

    private fun loadRoles() {
        val roles = listOf(AppUserRole.OWNER, AppUserRole.DRIVER)
        _rolesState.value = if (roles.isEmpty()) UiState.Empty else UiState.Success(roles)
    }

    fun selectRole(role: AppUserRole) {
        viewModelScope.launch {
            _selectionState.value = UiState.Loading
            repository.saveRole(role)
            _selectionState.value = UiState.Success(role)
        }
    }

    fun resetSelection() {
        _selectionState.value = UiState.Empty
    }
}
