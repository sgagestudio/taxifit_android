package com.mm.taxifit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.repository.RepositoryProvider
import com.mm.taxifit.data.repository.VehicleData
import com.mm.taxifit.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehicleDataViewModel : ViewModel() {
    private val repository = RepositoryProvider.onboardingRepository

    private val _submitState = MutableStateFlow<UiState<VehicleData>>(UiState.Empty)
    val submitState: StateFlow<UiState<VehicleData>> = _submitState.asStateFlow()

    fun submit(plate: String, licenseNumber: String, taximeterModel: String) {
        if (plate.isBlank() || licenseNumber.isBlank() || taximeterModel.isBlank()) {
            _submitState.value = UiState.Error("Completa todos los campos del vehiculo")
            return
        }

        val data = VehicleData(
            plate = plate.trim(),
            licenseNumber = licenseNumber.trim(),
            taximeterModel = taximeterModel.trim()
        )

        viewModelScope.launch {
            _submitState.value = UiState.Loading
            repository.saveVehicleData(data)
            _submitState.value = UiState.Success(data)
        }
    }

    fun resetState() {
        _submitState.value = UiState.Empty
    }
}
