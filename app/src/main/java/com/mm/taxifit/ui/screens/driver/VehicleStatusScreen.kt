package com.mm.taxifit.ui.screens.driver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.ui.screens.common.ErrorView
import com.mm.taxifit.ui.screens.common.LoadingView
import com.mm.taxifit.ui.state.UiState
import com.mm.taxifit.ui.viewmodels.VehicleStatusViewModel

@Composable
fun VehicleStatusScreen(
    vehicleStatusViewModel: VehicleStatusViewModel = viewModel()
) {
    val state by vehicleStatusViewModel.state.collectAsState()
    when (val value = state) {
        UiState.Empty -> ErrorView("No hay taxi asignado")
        UiState.Loading -> LoadingView()
        is UiState.Error -> ErrorView(value.message)
        is UiState.Success -> {
            val vehicle = value.data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Estado del vehiculo", style = MaterialTheme.typography.headlineMedium)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(vehicle.assignedTaxi, fontWeight = FontWeight.Bold)
                        Text("KM: ${vehicle.kilometers}")
                        Text(vehicle.nextInspection)
                    }
                }
            }
        }
    }
}
