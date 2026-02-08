package com.mm.taxifit.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.data.repository.AppUserRole
import com.mm.taxifit.ui.state.UiState
import com.mm.taxifit.ui.viewmodels.RoleSelectionViewModel

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (AppUserRole) -> Unit,
    roleSelectionViewModel: RoleSelectionViewModel = viewModel()
) {
    val rolesState by roleSelectionViewModel.rolesState.collectAsState()
    val selectionState by roleSelectionViewModel.selectionState.collectAsState()

    LaunchedEffect(selectionState) {
        val selected = (selectionState as? UiState.Success)?.data ?: return@LaunchedEffect
        onRoleSelected(selected)
        roleSelectionViewModel.resetSelection()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Seleccion de perfil", style = MaterialTheme.typography.headlineMedium)
        Text("Elige tu perfil para ajustar la experiencia", style = MaterialTheme.typography.bodyMedium)

        when (val state = rolesState) {
            UiState.Empty -> Text("Sin perfiles disponibles")
            UiState.Loading -> Text("Cargando perfiles...")
            is UiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
            is UiState.Success -> {
                state.data.forEach { role ->
                    val label = if (role == AppUserRole.OWNER) "Dueno" else "Conductor"
                    if (role == AppUserRole.OWNER) {
                        OutlinedButton(
                            onClick = { roleSelectionViewModel.selectRole(role) },
                            enabled = selectionState !is UiState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(minHeight = 52.dp)
                        ) {
                            Text(label)
                        }
                    } else {
                        Button(
                            onClick = { roleSelectionViewModel.selectRole(role) },
                            enabled = selectionState !is UiState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(minHeight = 52.dp)
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }
    }
}
