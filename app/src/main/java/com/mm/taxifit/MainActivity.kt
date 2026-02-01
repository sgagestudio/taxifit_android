package com.mm.taxifit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                val state by authViewModel.uiState.collectAsState()
                val isGoogleConfigured = BuildConfig.TAXIFIT_GOOGLE_WEB_CLIENT_ID.isNotBlank()
                val context = LocalContext.current

                Surface(modifier = Modifier.fillMaxSize()) {
                    when {
                        state.isLoading -> LoadingScreen()
                        state.isAuthenticated -> HomeScreen(onSignOut = authViewModel::signOut)
                        else -> LoginScreen(
                            errorMessage = state.errorMessage,
                            isGoogleConfigured = isGoogleConfigured,
                            onGoogleSignIn = { authViewModel.signInWithGoogle(context) }
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
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LoginScreen(
    errorMessage: String?,
    isGoogleConfigured: Boolean,
    onGoogleSignIn: () -> Unit
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
                text = "Taxifit",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Accede con tu cuenta",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!isGoogleConfigured) {
                Text(
                    text = "Configura TAXIFIT_GOOGLE_WEB_CLIENT_ID en gradle.properties",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedButton(
                onClick = onGoogleSignIn,
                enabled = isGoogleConfigured,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text(text = "Continuar con Google")
            }
        }
    }
}

@Composable
private fun HomeScreen(onSignOut: () -> Unit) {
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
                text = "Bienvenido a Taxifit",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Sesion activa",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onSignOut) {
                Text(text = "Cerrar sesion")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    TaxifitTheme {
        LoginScreen(
            errorMessage = null,
            isGoogleConfigured = true,
            onGoogleSignIn = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    TaxifitTheme {
        HomeScreen(onSignOut = {})
    }
}
