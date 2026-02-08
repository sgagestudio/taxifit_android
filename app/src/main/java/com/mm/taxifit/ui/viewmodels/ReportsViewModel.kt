package com.mm.taxifit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mm.taxifit.data.repository.ReportMonth
import com.mm.taxifit.data.repository.RepositoryProvider
import com.mm.taxifit.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportsViewModel : ViewModel() {
    private val repository = RepositoryProvider.ownerRepository

    private val _monthsState = MutableStateFlow<UiState<List<ReportMonth>>>(UiState.Loading)
    val monthsState: StateFlow<UiState<List<ReportMonth>>> = _monthsState.asStateFlow()

    private val _exportState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val exportState: StateFlow<UiState<String>> = _exportState.asStateFlow()

    init {
        loadMonths()
    }

    fun loadMonths() {
        viewModelScope.launch {
            _monthsState.value = UiState.Loading
            val months = repository.getReportMonths()
            _monthsState.value = if (months.isEmpty()) UiState.Empty else UiState.Success(months)
        }
    }

    fun exportReport(month: ReportMonth) {
        viewModelScope.launch {
            _exportState.value = UiState.Loading
            val exported = repository.exportReport(month)
            _exportState.value = if (exported) {
                UiState.Success("Reporte ${month.title} exportado")
            } else {
                UiState.Error("No se pudo exportar el reporte")
            }
        }
    }

    fun clearExportState() {
        _exportState.value = UiState.Empty
    }
}
