package com.mm.taxifit.ui.screens.common

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.HomeDriverActivity
import com.mm.taxifit.HomeOwnerActivity
import com.mm.taxifit.MainActivity
import com.mm.taxifit.data.repository.AppUserRole
import com.mm.taxifit.domain.model.Role
import com.mm.taxifit.ui.state.UiState
import com.mm.taxifit.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    currentViewRole: AppUserRole,
    onOpenProfile: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val profileState by settingsViewModel.profileState.collectAsState()
    val signOutState by settingsViewModel.signOutState.collectAsState()
    val viewChangeState by settingsViewModel.viewChangeState.collectAsState()
    val upgradeState by settingsViewModel.upgradeState.collectAsState()

    var showActivateDualDialog by remember { mutableStateOf(false) }
    var showLicenseDialog by remember { mutableStateOf(false) }
    var newLicenseForDual by remember { mutableStateOf("") }

    LaunchedEffect(signOutState) {
        if (signOutState is UiState.Success) {
            settingsViewModel.clearSignOutState()
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(viewChangeState) {
        val targetRole = (viewChangeState as? UiState.Success)?.data ?: return@LaunchedEffect
        settingsViewModel.clearViewChangeState()
        val destination = when (targetRole) {
            Role.DUENO -> HomeOwnerActivity::class.java
            Role.CONDUCTOR, Role.DUENO_CONDUCTOR -> HomeDriverActivity::class.java
        }
        val intent = Intent(context, destination).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
        (context as? Activity)?.finish()
    }

    LaunchedEffect(upgradeState) {
        val upgraded = (upgradeState as? UiState.Success)?.data ?: return@LaunchedEffect
        if (upgraded.role == Role.DUENO_CONDUCTOR) {
            val target = if (currentViewRole == AppUserRole.OWNER) Role.CONDUCTOR else Role.DUENO
            settingsViewModel.clearUpgradeState()
            settingsViewModel.switchViewTo(target)
        }
    }

    when (val state = profileState) {
        UiState.Loading -> LoadingView("Cargando ajustes...")
        UiState.Empty -> EmptyView("No hay datos de ajustes")
        is UiState.Error -> ErrorView(state.message)
        is UiState.Success -> {
            val profile = state.data
            val switchTargetRole = if (currentViewRole == AppUserRole.OWNER) Role.CONDUCTOR else Role.DUENO
            val switchTargetLabel = if (switchTargetRole == Role.DUENO) "dueno" else "conductor"
            val isDualProfile = profile.role == Role.DUENO_CONDUCTOR

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Ajustes", style = MaterialTheme.typography.headlineMedium)

                Button(
                    onClick = onOpenProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 52.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "Perfil")
                    Text(" Perfil")
                }

                Spacer(modifier = Modifier.weight(1f))

                if (viewChangeState is UiState.Error) {
                    Text(
                        text = (viewChangeState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (upgradeState is UiState.Error) {
                    Text(
                        text = (upgradeState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (signOutState is UiState.Error) {
                    Text(
                        text = (signOutState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = {
                        if (isDualProfile) {
                            settingsViewModel.switchViewTo(switchTargetRole)
                        } else {
                            showActivateDualDialog = true
                        }
                    },
                    enabled = viewChangeState !is UiState.Loading && upgradeState !is UiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 52.dp)
                ) {
                    val label = if (isDualProfile) {
                        "Cambiar de vista a $switchTargetLabel"
                    } else {
                        "Cambiar de vista"
                    }
                    Text(label)
                }

                Button(
                    onClick = { settingsViewModel.signOut() },
                    enabled = signOutState !is UiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 52.dp)
                ) {
                    Text(if (signOutState is UiState.Loading) "Cerrando sesion..." else "Cerrar sesion")
                }
            }

            if (showActivateDualDialog) {
                val dialogText = when (profile.role) {
                    Role.CONDUCTOR -> {
                        "Para cambiar de vista debes activar el perfil dueno_conductor. " +
                            "Se solicitara tu numero licencia de taxi."
                    }

                    Role.DUENO -> {
                        "Para cambiar de vista debes activar el perfil dueno_conductor. " +
                            "Se creara tu perfil conductor automaticamente."
                    }

                    Role.DUENO_CONDUCTOR -> "Se cambiara la vista activa."
                }

                AlertDialog(
                    onDismissRequest = { showActivateDualDialog = false },
                    title = { Text("Activar perfil dual") },
                    text = { Text(dialogText) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showActivateDualDialog = false
                                when (profile.role) {
                                    Role.CONDUCTOR -> showLicenseDialog = true
                                    Role.DUENO -> settingsViewModel.promoteOwnerToHybrid()
                                    Role.DUENO_CONDUCTOR -> settingsViewModel.switchViewTo(switchTargetRole)
                                }
                            }
                        ) {
                            Text("Si")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showActivateDualDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }

            if (showLicenseDialog) {
                AlertDialog(
                    onDismissRequest = { showLicenseDialog = false },
                    title = { Text("Onboarding basico") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Introduce tu numero de licencia para activar perfil dual.")
                            OutlinedTextField(
                                value = newLicenseForDual,
                                onValueChange = { newLicenseForDual = it },
                                label = { Text("Numero licencia de taxi") },
                                singleLine = true,
                                isError = newLicenseForDual.isBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                settingsViewModel.promoteConductorToHybrid(newLicenseForDual)
                                showLicenseDialog = false
                            }
                        ) {
                            Text("Continuar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLicenseDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
