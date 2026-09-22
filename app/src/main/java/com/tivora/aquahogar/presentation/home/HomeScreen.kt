package com.tivora.aquahogar.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tivora.aquahogar.domain.model.EstadoMeta
import com.tivora.aquahogar.domain.model.ResumenHome
import com.tivora.aquahogar.presentation.theme.AlertHigh
import com.tivora.aquahogar.presentation.theme.AlertOk
import com.tivora.aquahogar.presentation.theme.AlertWarn
import com.tivora.aquahogar.presentation.theme.Aqua
import kotlin.math.min

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        when {
            state.cargando -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Aqua
            )
            state.error != null -> Text(
                text = state.error ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
            state.resumen != null -> HomeContent(state.resumen!!, onCerrarSesion = viewModel::cerrarSesion)
        }
    }
}

@Composable
private fun HomeContent(resumen: ResumenHome, onCerrarSesion: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("AquaHogar IA", style = MaterialTheme.typography.labelLarge, color = Aqua)
            TextButton(onClick = onCerrarSesion) { Text("Salir") }
        }
        Spacer(Modifier.height(4.dp))
        Text(resumen.vivienda.nombre, style = MaterialTheme.typography.headlineMedium)
        Text(
            "${resumen.vivienda.numeroHabitantes} habitantes · meta ${formatLitros(resumen.vivienda.metaMensualLitros)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))

        if (!resumen.hayLecturas) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    "Registra al menos dos lecturas del medidor para calcular el consumo de hoy y el acumulado mensual.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Today,
                titulo = "Hoy",
                valor = formatLitros(resumen.consumoHoyLitros)
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.CalendarMonth,
                titulo = "Mes",
                valor = formatLitros(resumen.acumuladoMensualLitros)
            )
        }
        Spacer(Modifier.height(12.dp))
        MetricCard(
            modifier = Modifier.fillMaxWidth(),
                icon = Icons.AutoMirrored.Outlined.ShowChart,
            titulo = "Promedio diario del mes",
            valor = formatLitros(resumen.promedioDiarioLitros)
        )
        Spacer(Modifier.height(16.dp))
        MetaCard(resumen)
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier,
    icon: ImageVector,
    titulo: String,
    valor: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Aqua.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Aqua, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(titulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun MetaCard(resumen: ResumenHome) {
    val color = when (resumen.estadoMeta) {
        EstadoMeta.DENTRO -> AlertOk
        EstadoMeta.CERCA -> AlertWarn
        EstadoMeta.EXCEDIDA -> AlertHigh
    }
    val etiqueta = when (resumen.estadoMeta) {
        EstadoMeta.DENTRO -> "Dentro de la meta"
        EstadoMeta.CERCA -> "Cerca del límite"
        EstadoMeta.EXCEDIDA -> "Meta excedida"
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = color)
                Spacer(Modifier.size(8.dp))
                Text("Estado de la meta", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(etiqueta, color = color, style = MaterialTheme.typography.titleLarge)
            Text(
                "${"%.0f".format(resumen.porcentajeMeta)}% de ${formatLitros(resumen.vivienda.metaMensualLitros)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { min(1f, (resumen.porcentajeMeta / 100.0).toFloat()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(20.dp)),
                color = color,
                trackColor = Color.White.copy(alpha = 0.12f),
                strokeCap = StrokeCap.Round,
                drawStopIndicator = {}
            )
        }
    }
}

private fun formatLitros(value: Double): String = "${"%.0f".format(value)} L"
