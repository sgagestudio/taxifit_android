package com.mm.taxifit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.repository.ExpenseItem
import com.mm.taxifit.data.repository.RepositoryProvider
import com.mm.taxifit.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FinancialsViewModel : ViewModel() {
    private val repository = RepositoryProvider.ownerRepository
    private var cachedItems: List<ExpenseItem> = emptyList()

    private val _state = MutableStateFlow<UiState<List<ExpenseItem>>>(UiState.Loading)
    val state: StateFlow<UiState<List<ExpenseItem>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            cachedItems = repository.getExpenses()
            _state.value = if (cachedItems.isEmpty()) UiState.Empty else UiState.Success(cachedItems)
        }
    }

    fun toggleValidation(id: String) {
        cachedItems = cachedItems.map { item ->
            if (item.id == id) item.copy(isValidated = !item.isValidated) else item
        }
        _state.value = if (cachedItems.isEmpty()) UiState.Empty else UiState.Success(cachedItems)
    }
}
