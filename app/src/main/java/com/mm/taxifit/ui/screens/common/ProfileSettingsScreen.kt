package com.mm.taxifit.ui.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.data.repository.AppUserRole
import com.mm.taxifit.ui.state.UiState
import com.mm.taxifit.ui.viewmodels.SettingsViewModel

@Composable
fun ProfileSettingsScreen(
    currentViewRole: AppUserRole,
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val profileState by settingsViewModel.profileState.collectAsState()
    val saveState by settingsViewModel.saveState.collectAsState()

    when (val state = profileState) {
        UiState.Loading -> LoadingView("Cargando perfil...")
        UiState.Empty -> EmptyView("No hay perfil disponible")
        is UiState.Error -> ErrorView(state.message)
        is UiState.Success -> {
            val profile = state.data
            val showWorkLicense = currentViewRole == AppUserRole.OWNER

            var email by remember(profile.userId) { mutableStateOf(profile.email) }
            var dni by remember(profile.userId) { mutableStateOf(profile.dni) }
            var fullName by remember(profile.userId) { mutableStateOf(profile.fullName) }
            var phone by remember(profile.userId) { mutableStateOf(profile.phone) }
            var workLicense by remember(profile.userId) { mutableStateOf(profile.workLicense.orEmpty()) }

            LaunchedEffect(profile.userId, currentViewRole) {
                settingsViewModel.clearSaveState()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                    Text(
                        text = "Perfil",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    isError = email.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dni,
                    onValueChange = { dni = it },
                    label = { Text("DNI") },
                    singleLine = true,
                    isError = dni.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    isError = fullName.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefono") },
                    singleLine = true,
                    isError = phone.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showWorkLicense) {
                    OutlinedTextField(
                        value = workLicense,
                        onValueChange = { workLicense = it },
                        label = { Text("Numero licencia de taxi") },
                        singleLine = true,
                        isError = workLicense.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (saveState is UiState.Error) {
                    Text(
                        text = (saveState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (saveState is UiState.Success) {
                    Text(
                        text = (saveState as UiState.Success).data,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = {
                        settingsViewModel.saveProfile(
                            current = profile,
                            email = email,
                            dni = dni,
                            fullName = fullName,
                            phone = phone,
                            workLicense = if (showWorkLicense) workLicense else profile.workLicense.orEmpty(),
                            validateWorkLicense = showWorkLicense
                        )
                    },
                    enabled = saveState !is UiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 52.dp)
                ) {
                    Text(if (saveState is UiState.Loading) "Guardando..." else "Guardar cambios")
                }
            }
        }
    }
}
