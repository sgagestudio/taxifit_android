package com.mm.taxifit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.repository.PersonalData
import com.mm.taxifit.data.repository.RepositoryProvider
import com.mm.taxifit.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalDataViewModel : ViewModel() {
    private val repository = RepositoryProvider.onboardingRepository

    private val _submitState = MutableStateFlow<UiState<PersonalData>>(UiState.Empty)
    val submitState: StateFlow<UiState<PersonalData>> = _submitState.asStateFlow()

    fun submit(fullName: String, dni: String, phone: String) {
        if (fullName.isBlank() || dni.isBlank() || phone.isBlank()) {
            _submitState.value = UiState.Error("Todos los campos son obligatorios")
            return
        }

        val data = PersonalData(
            fullName = fullName.trim(),
            dni = dni.trim(),
            phone = phone.trim()
        )

        viewModelScope.launch {
            _submitState.value = UiState.Loading
            repository.savePersonalData(data)
            _submitState.value = UiState.Success(data)
        }
    }

    fun resetState() {
        _submitState.value = UiState.Empty
    }
}
