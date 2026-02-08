package com.mm.taxifit.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.ui.screens.common.ErrorView
import com.mm.taxifit.ui.screens.common.LoadingView
import com.mm.taxifit.ui.state.UiState
import com.mm.taxifit.ui.viewmodels.HomeDriverViewModel

@Composable
fun HomeDriverScreen(
    homeDriverViewModel: HomeDriverViewModel = viewModel()
) {
    val state by homeDriverViewModel.state.collectAsState()
    when (val value = state) {
        UiState.Loading -> LoadingView()
        UiState.Empty -> ErrorView("No hay estado de jornada")
        is UiState.Error -> ErrorView(value.message)
        is UiState.Success -> {
            val data = value.data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF101820))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Panel Conductor",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 4.dp,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = data.shiftState,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cronometro: ${data.elapsed}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Button(
                    onClick = {},
                    modifier = Modifier.size(220.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF4B400),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = data.mainAction,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Boton de alto contraste y tamano > 48dp para uso en ruta",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
