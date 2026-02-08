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
import com.mm.taxifit.ui.viewmodels.PersonalDataViewModel

@Composable
fun PersonalDataScreen(
    onContinue: () -> Unit,
    personalDataViewModel: PersonalDataViewModel = viewModel()
) {
    val submitState by personalDataViewModel.submitState.collectAsState()
    var fullName by rememberSaveable { mutableStateOf("") }
    var dni by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var hasAttempted by remember { mutableStateOf(false) }

    LaunchedEffect(submitState) {
        if (submitState is UiState.Success) {
            onContinue()
            personalDataViewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Datos personales", style = MaterialTheme.typography.headlineMedium)
        Text("Completa tu informacion basica", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Nombre completo") },
            singleLine = true,
            isError = hasAttempted && fullName.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = dni,
            onValueChange = { dni = it },
            label = { Text("DNI") },
            singleLine = true,
            isError = hasAttempted && dni.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefono") },
            singleLine = true,
            isError = hasAttempted && phone.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        val errorMessage = (submitState as? UiState.Error)?.message
        if (errorMessage != null) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                hasAttempted = true
                personalDataViewModel.submit(fullName, dni, phone)
            },
            enabled = submitState !is UiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 52.dp)
        ) {
            Text(if (submitState is UiState.Loading) "Guardando..." else "Continuar")
        }
    }
}
