package com.mm.taxifit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.repository.FleetAssignment
import com.mm.taxifit.data.repository.RepositoryProvider
import com.mm.taxifit.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FleetManagementViewModel : ViewModel() {
    private val repository = RepositoryProvider.ownerRepository

    private val _state = MutableStateFlow<UiState<List<FleetAssignment>>>(UiState.Loading)
    val state: StateFlow<UiState<List<FleetAssignment>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val list = repository.getFleetAssignments()
            _state.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
        }
    }
}
