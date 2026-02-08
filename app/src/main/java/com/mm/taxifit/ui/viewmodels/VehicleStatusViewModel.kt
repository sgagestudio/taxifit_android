package com.mm.taxifit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.repository.RepositoryProvider
import com.mm.taxifit.data.repository.VehicleStatus
import com.mm.taxifit.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehicleStatusViewModel : ViewModel() {
    private val repository = RepositoryProvider.driverRepository

    private val _state = MutableStateFlow<UiState<VehicleStatus>>(UiState.Loading)
    val state: StateFlow<UiState<VehicleStatus>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val vehicleStatus = repository.getVehicleStatus()
            _state.value = UiState.Success(vehicleStatus)
        }
    }
}
