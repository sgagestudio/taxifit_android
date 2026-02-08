package com.mm.taxifit.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mm.taxifit.ui.screens.common.EmptyView
import com.mm.taxifit.ui.screens.common.ErrorView
import com.mm.taxifit.ui.screens.common.LoadingView
import com.mm.taxifit.ui.state.UiState
import com.mm.taxifit.ui.viewmodels.GoalsViewModel
import kotlin.math.max

@Composable
fun GoalsScreen(
    goalsViewModel: GoalsViewModel = viewModel()
) {
    val state by goalsViewModel.state.collectAsState()
    when (val value = state) {
        UiState.Empty -> EmptyView("No hay objetivos configurados")
        UiState.Loading -> LoadingView()
        is UiState.Error -> ErrorView(value.message)
        is UiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Objetivos semanales", style = MaterialTheme.typography.headlineMedium)
                value.data.forEach { goal ->
                    val ratio = goal.income.toFloat() / max(1, goal.target)
                    Text(
                        text = "${goal.label}: ${goal.income} / ${goal.target}",
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(Color(0xFFE6E6E6))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = ratio.coerceIn(0f, 1f))
                                .height(20.dp)
                                .background(
                                    if (goal.income >= goal.target) Color(0xFF2E7D32) else Color(
                                        0xFFF9A825
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}
