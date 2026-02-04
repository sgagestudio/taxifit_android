package com.mm.taxifit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.auth.AuthState
import com.mm.taxifit.auth.AuthViewModel
import com.mm.taxifit.auth.SupabaseProvider
import com.mm.taxifit.ui.theme.TaxifitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SupabaseProvider.handleDeepLink(intent)
        setContent {
            TaxifitTheme {
                val authViewModel: AuthViewModel = viewModel()
                val state by authViewModel.state.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val current = state) {
                        AuthState.Loading -> SplashScreen()
                        is AuthState.LoggedIn -> HomeScreen(
                            email = current.email,
                            onSignOut = authViewModel::signOut
                        )
                        is AuthState.NeedsEmailVerification -> VerifyEmailScreen(
                            email = current.email,
                            onResend = authViewModel::resendVerification,
                            onBackToLogin = authViewModel::backToLogin
                        )
                        is AuthState.LoggedOut -> AuthScreen(
                            errorMessage = null,
                            canResend = false,
                            onSignIn = authViewModel::signIn,
                            onSignUp = authViewModel::signUp,
                            onResend = authViewModel::resendVerification
                        )
                        is AuthState.Error -> AuthScreen(
                            errorMessage = current.message,
                            canResend = current.canResend,
                            onSignIn = authViewModel::signIn,
                            onSignUp = authViewModel::signUp,
                            onResend = authViewModel::resendVerification,
                            presetEmail = current.email
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        SupabaseProvider.handleDeepLink(intent)
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AuthScreen(
    errorMessage: String?,
    canResend: Boolean,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit,
    onResend: (String) -> Unit,
    presetEmail: String? = null
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf(presetEmail ?: "") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isSignUp) "Registro" else "Login",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    localError = null
                },
                label = { Text("Email") },
                singleLine = true
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    localError = null
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            if (isSignUp) {
                val passwordMismatch = confirmPassword.isNotEmpty() && confirmPassword != password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        localError = null
                    },
                    label = { Text("Confirmar password") },
                    singleLine = true,
                    isError = passwordMismatch,
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            if (errorMessage != null) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }
            if (localError != null) {
                Text(text = localError ?: "", color = MaterialTheme.colorScheme.error)
            }

            Button(onClick = {
                if (isSignUp) {
                    if (password != confirmPassword) {
                        localError = "Las contrasenas no coinciden"
                        return@Button
                    }
                    onSignUp(email, password)
                } else {
                    onSignIn(email, password)
                }
            }) {
                Text("Conectar")
            }

            if (canResend && email.isNotBlank()) {
                TextButton(onClick = { onResend(email) }) {
                    Text("Reenviar verificacion")
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isSignUp) {
                    Text("Ya tienes una cuenta?")
                    TextButton(onClick = {
                        isSignUp = false
                        confirmPassword = ""
                        localError = null
                    }) {
                        Text("Inicia sesion aqui", color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Text("Aun no tienes cuenta?")
                    TextButton(onClick = {
                        isSignUp = true
                        localError = null
                    }) {
                        Text("Registrate aqui", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun VerifyEmailScreen(
    email: String,
    onResend: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Verifica tu email",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Te enviamos un email a $email para verificar tu cuenta.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Abre el enlace para activar tu cuenta.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onResend(email) }) {
                Text("Reenviar verificacion")
            }
            TextButton(onClick = onBackToLogin) {
                Text("Volver a login")
            }
        }
    }
}

@Composable
private fun HomeScreen(
    email: String?,
    onSignOut: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Bienvenido a TaxiFit",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = email ?: "Sesion activa",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = onSignOut) {
                Text("Cerrar sesion")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthPreview() {
    TaxifitTheme {
        AuthScreen(
            errorMessage = null,
            canResend = false,
            onSignIn = { _, _ -> },
            onSignUp = { _, _ -> },
            onResend = {}
        )
    }
}
