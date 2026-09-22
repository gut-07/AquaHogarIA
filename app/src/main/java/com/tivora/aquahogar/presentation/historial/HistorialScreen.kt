package com.tivora.aquahogar.presentation.historial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.tivora.aquahogar.domain.model.PeriodoHistorial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(viewModel: HistorialViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val periodos = PeriodoHistorial.entries
    val selectedIndex = periodos.indexOf(state.periodo).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text("Historial y estadísticas", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        PrimaryTabRow(selectedTabIndex = selectedIndex) {
            periodos.forEach { periodo ->
                Tab(
                    selected = state.periodo == periodo,
                    onClick = { viewModel.onPeriodoChange(periodo) },
                    text = {
                        Text(
                            when (periodo) {
                                PeriodoHistorial.DIARIO -> "Diario"
                                PeriodoHistorial.SEMANAL -> "Semanal"
                                PeriodoHistorial.MENSUAL -> "Mensual"
                            }
                        )
                    }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        if (state.puntos.isEmpty()) {
            Text(
                "Todavía no hay consumos calculados. Necesitas al menos dos lecturas consecutivas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val values = remember(state.puntos) { state.puntos.map { it.litros.toFloat() } }
            ProvideChartStyle(m3ChartStyle()) {
                Chart(
                    chart = columnChart(),
                    model = entryModelOf(*values.toTypedArray()),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.puntos.asReversed()) { punto ->
                    Column {
                        Text(punto.etiqueta, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${"%.0f".format(punto.litros)} L",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
