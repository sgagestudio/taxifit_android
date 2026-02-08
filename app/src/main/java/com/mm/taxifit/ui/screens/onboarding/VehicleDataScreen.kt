package com.mm.taxifit.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.ui.state.UiState
import com.mm.taxifit.ui.viewmodels.VehicleDataViewModel

@Composable
fun VehicleDataScreen(
    onContinue: () -> Unit,
    vehicleDataViewModel: VehicleDataViewModel = viewModel()
) {
    val submitState by vehicleDataViewModel.submitState.collectAsState()
    var plate by rememberSaveable { mutableStateOf("") }
    var licenseNumber by rememberSaveable { mutableStateOf("") }
    var taximeterModel by rememberSaveable { mutableStateOf("") }
    var hasAttempted by remember { mutableStateOf(false) }

    LaunchedEffect(submitState) {
        if (submitState is UiState.Success) {
            onContinue()
            vehicleDataViewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Datos del vehiculo", style = MaterialTheme.typography.headlineMedium)
        Text("Este paso aplica para perfil Dueno", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = plate,
            onValueChange = { plate = it },
            label = { Text("Matricula") },
            singleLine = true,
            isError = hasAttempted && plate.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = licenseNumber,
            onValueChange = { licenseNumber = it },
            label = { Text("Num. licencia") },
            singleLine = true,
            isError = hasAttempted && licenseNumber.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = taximeterModel,
            onValueChange = { taximeterModel = it },
            label = { Text("Modelo taximetro") },
            singleLine = true,
            isError = hasAttempted && taximeterModel.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        val errorMessage = (submitState as? UiState.Error)?.message
        if (errorMessage != null) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                hasAttempted = true
                vehicleDataViewModel.submit(plate, licenseNumber, taximeterModel)
            },
            enabled = submitState !is UiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 52.dp)
        ) {
            Text(if (submitState is UiState.Loading) "Guardando..." else "Finalizar onboarding")
        }
    }
}
