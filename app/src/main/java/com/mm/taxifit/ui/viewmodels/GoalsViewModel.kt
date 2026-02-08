package com.mm.taxifit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.repository.GoalProgress
import com.mm.taxifit.data.repository.RepositoryProvider
import com.mm.taxifit.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalsViewModel : ViewModel() {
    private val repository = RepositoryProvider.driverRepository

    private val _state = MutableStateFlow<UiState<List<GoalProgress>>>(UiState.Loading)
    val state: StateFlow<UiState<List<GoalProgress>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val goals = repository.getGoals()
            _state.value = if (goals.isEmpty()) UiState.Empty else UiState.Success(goals)
        }
    }
}
