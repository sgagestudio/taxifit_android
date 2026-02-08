package com.mm.taxifit.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.ui.screens.common.EmptyView
import com.mm.taxifit.ui.screens.common.ErrorView
import com.mm.taxifit.ui.screens.common.LoadingView
import com.mm.taxifit.ui.state.UiState
import com.mm.taxifit.ui.viewmodels.FinancialsViewModel

@Composable
fun FinancialsScreen(
    financialsViewModel: FinancialsViewModel = viewModel()
) {
    val state by financialsViewModel.state.collectAsState()
    when (val value = state) {
        UiState.Empty -> EmptyView("No hay gastos cargados")
        UiState.Loading -> LoadingView()
        is UiState.Error -> ErrorView(value.message)
        is UiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Finanzas", style = MaterialTheme.typography.headlineMedium)
                }
                items(value.data) { expense ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFFEDE7F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("IMG")
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(expense.concept, style = MaterialTheme.typography.titleMedium)
                                Text("Importe: ${expense.amount}")
                                Text("Ticket: ${expense.receiptPreview}")
                            }
                            if (expense.isValidated) {
                                OutlinedButton(onClick = { financialsViewModel.toggleValidation(expense.id) }) {
                                    Text("Validado")
                                }
                            } else {
                                Button(onClick = { financialsViewModel.toggleValidation(expense.id) }) {
                                    Text("Validar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
