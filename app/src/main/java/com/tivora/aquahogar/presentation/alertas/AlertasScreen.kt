package com.tivora.aquahogar.presentation.alertas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tivora.aquahogar.core.time.DateUtils
import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.model.NivelAlerta
import com.tivora.aquahogar.domain.usecase.GenerarAlertaUseCase
import com.tivora.aquahogar.presentation.theme.AlertHigh
import com.tivora.aquahogar.presentation.theme.AlertOk
import com.tivora.aquahogar.presentation.theme.AlertWarn

@Composable
fun AlertasScreen(viewModel: AlertasViewModel = hiltViewModel()) {
    val alertas by viewModel.alertas.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text("Alertas y recomendaciones", style = MaterialTheme.typography.headlineMedium)
        Text(
            "ALTO si el incremento es mayor al 50%. PRECAUCIÓN si supera el 30%.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        if (alertas.isEmpty()) {
            Text(
                "Cuando registres lecturas consecutivas verás aquí el patrón de variación.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(alertas, key = { it.id }) { alerta ->
                    AlertaCard(alerta)
                }
            }
        }
    }
}

@Composable
private fun AlertaCard(alerta: AlertaConsumo) {
    val color = when (alerta.nivel) {
        NivelAlerta.ALTO -> AlertHigh
        NivelAlerta.PRECAUCION -> AlertWarn
        NivelAlerta.NORMAL -> AlertOk
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(alerta.nivel.name, color = color, style = MaterialTheme.typography.labelLarge)
            Text(DateUtils.format(alerta.fecha), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
            Text(alerta.descripcion, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                "Acción sugerida: ${GenerarAlertaUseCase.recomendacion(alerta.nivel)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
