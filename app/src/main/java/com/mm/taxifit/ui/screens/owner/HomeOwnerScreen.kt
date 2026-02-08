package com.mm.taxifit.ui.screens.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.ui.screens.common.EmptyView
import com.mm.taxifit.ui.screens.common.ErrorView
import com.mm.taxifit.ui.screens.common.LoadingView
import com.mm.taxifit.ui.state.UiState
import com.mm.taxifit.ui.viewmodels.HomeOwnerViewModel

@Composable
fun HomeOwnerScreen(
    homeOwnerViewModel: HomeOwnerViewModel = viewModel()
) {
    val state by homeOwnerViewModel.state.collectAsState()
    when (val value = state) {
        UiState.Empty -> EmptyView("No hay KPIs disponibles")
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
                    Text("Panel Dueno", style = MaterialTheme.typography.headlineMedium)
                }
                items(value.data) { kpi ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(kpi.title, style = MaterialTheme.typography.titleMedium)
                            Text(kpi.value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(kpi.hint, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
