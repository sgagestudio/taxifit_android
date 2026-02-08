package com.mm.taxifit

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mm.taxifit.auth.SupabaseProvider
import com.mm.taxifit.data.local.LocalUserEntity
import com.mm.taxifit.data.local.LocalUserStorage
import com.mm.taxifit.data.local.UserPreferences
import com.mm.taxifit.data.remote.RemoteDriverInsert
import com.mm.taxifit.data.remote.RemoteOwnerInsert
import com.mm.taxifit.data.remote.RemoteUserInsert
import com.mm.taxifit.domain.model.Role
import com.mm.taxifit.ui.theme.TaxifitTheme
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaxifitTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    OnboardingScreen(
                        onCompleted = { role ->
                            openHomeForRole(role)
                        }
                    )
                }
            }
        }
    }

    private fun openHomeForRole(role: Role) {
        val destination = when (role) {
            Role.DUENO -> HomeOwnerActivity::class.java
            Role.CONDUCTOR, Role.DUENO_CONDUCTOR -> HomeDriverActivity::class.java
        }
        startActivity(Intent(this, destination))
        finish()
    }
}

@Composable
private fun OnboardingScreen(
    onCompleted: (Role) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val localUserStorage = remember { LocalUserStorage(context) }
    val userPreferences = remember { UserPreferences(context) }

    var localUser by remember { mutableStateOf<LocalUserEntity?>(null) }
    var selectedRole by remember { mutableStateOf<Role?>(null) }
    var fullName by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        localUser = withContext(Dispatchers.IO) { localUserStorage.load() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Dinos tu perfil.",
                style = MaterialTheme.typography.headlineMedium
            )

            if (selectedRole == null) {
                Button(
                    onClick = { selectedRole = Role.DUENO },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dueño con licencia")
                }
                Button(
                    onClick = { selectedRole = Role.CONDUCTOR },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Conductor sin licencia")
                }
                Button(
                    onClick = { selectedRole = Role.DUENO_CONDUCTOR },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dueño Conductor")
                }
            } else {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre Completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dni,
                    onValueChange = { dni = it },
                    label = { Text("DNI") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono Móvil") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedRole == Role.DUENO || selectedRole == Role.DUENO_CONDUCTOR) {
                    OutlinedTextField(
                        value = licenseNumber,
                        onValueChange = { licenseNumber = it },
                        label = { Text("Número de licencia") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorMessage != null) {
                    Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = {
                        val role = selectedRole ?: return@Button
                        val user = localUser
                        if (user == null) {
                            errorMessage = "No se encontro usuario local"
                            return@Button
                        }
                        if (fullName.isBlank() || dni.isBlank() || phone.isBlank()) {
                            errorMessage = "Completa todos los campos"
                            return@Button
                        }
                        if ((role == Role.DUENO || role == Role.DUENO_CONDUCTOR) && licenseNumber.isBlank()) {
                            errorMessage = "Completa el número de licencia"
                            return@Button
                        }
                        errorMessage = null
                        isLoading = true
                        scope.launch {
                            try {
                                val supabase = SupabaseProvider.client
                                val userInsert = RemoteUserInsert(
                                    id = user.id,
                                    email = user.email,
                                    dni = dni,
                                    role = role.dbValue,
                                    fullname = fullName,
                                    phone = phone
                                )
                                supabase.postgrest["users"].insert(userInsert)

                                if (role == Role.DUENO || role == Role.DUENO_CONDUCTOR) {
                                    val ownerInsert = RemoteOwnerInsert(
                                        userId = user.id,
                                        workLicense = licenseNumber
                                    )
                                    supabase.postgrest["owners"].insert(ownerInsert)
                                }

                                if (role == Role.CONDUCTOR || role == Role.DUENO_CONDUCTOR) {
                                    val driverInsert = RemoteDriverInsert(
                                        userId = user.id,
                                        ownerId = if (role == Role.DUENO_CONDUCTOR) user.id else null
                                    )
                                    supabase.postgrest["drivers"].insert(driverInsert)
                                }

                                val lastRole = if (role == Role.DUENO) Role.DUENO else Role.CONDUCTOR
                                withContext(Dispatchers.IO) {
                                    userPreferences.saveLastRole(lastRole)
                                }
                                onCompleted(lastRole)
                            } catch (ex: Exception) {
                                errorMessage = ex.message ?: "Error guardando datos"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Conectar")
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(4.dp))
                    CircularProgressIndicator()
                }

            }
        }
    }
}
