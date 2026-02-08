package com.mm.taxifit.ui.screens.owner

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.data.repository.ReportMonth
import com.mm.taxifit.ui.screens.common.EmptyView
import com.mm.taxifit.ui.screens.common.ErrorView
import com.mm.taxifit.ui.screens.common.LoadingView
import com.mm.taxifit.ui.state.UiState
import com.mm.taxifit.ui.viewmodels.ReportsViewModel

@Composable
fun ReportsScreen(
    reportsViewModel: ReportsViewModel = viewModel()
) {
    val context = LocalContext.current
    val monthsState by reportsViewModel.monthsState.collectAsState()
    val exportState by reportsViewModel.exportState.collectAsState()
    val selectedMonth = remember { mutableStateOf<ReportMonth?>(null) }

    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                reportsViewModel.clearExportState()
            }
            is UiState.Success -> {
                Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()
                reportsViewModel.clearExportState()
            }
            else -> Unit
        }
    }

    when (val value = monthsState) {
        UiState.Empty -> EmptyView("No hay meses disponibles")
        UiState.Loading -> LoadingView()
        is UiState.Error -> ErrorView(value.message)
        is UiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Reportes", style = MaterialTheme.typography.headlineMedium)
                    Text("Selecciona un mes para exportar", style = MaterialTheme.typography.bodyMedium)
                }
                items(value.data) { month ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(month.title, style = MaterialTheme.typography.titleMedium)
                            if (selectedMonth.value?.key == month.key) {
                                Button(onClick = { selectedMonth.value = month }) {
                                    Text("Seleccionado")
                                }
                            } else {
                                OutlinedButton(onClick = { selectedMonth.value = month }) {
                                    Text("Seleccionar")
                                }
                            }
                        }
                    }
                }
                item {
                    val month = selectedMonth.value
                    Button(
                        onClick = {
                            if (month != null) {
                                reportsViewModel.exportReport(month)
                            } else {
                                Toast.makeText(context, "Selecciona un mes", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = exportState !is UiState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (exportState is UiState.Loading) "Exportando..." else "Exportar reporte")
                    }
                }
            }
        }
    }
}
