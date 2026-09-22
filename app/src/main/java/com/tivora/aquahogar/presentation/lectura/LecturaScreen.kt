package com.tivora.aquahogar.presentation.lectura

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tivora.aquahogar.core.time.DateUtils
import com.tivora.aquahogar.presentation.theme.Aqua
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturaScreen(viewModel: RegistrarLecturaViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showPicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.mensajeExito) {
        if (state.mensajeExito != null) {
            delay(4_000)
            viewModel.limpiarMensaje()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar lectura", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Ingresa la lectura actual del medidor en m³. No puede ser menor que la anterior.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(16.dp)) {
                Text("Última lectura", style = MaterialTheme.typography.labelLarge, color = Aqua)
                val anterior = state.lecturaAnteriorM3
                Text(
                    if (anterior == null) {
                        "Aún no hay lecturas. Esta será la referencia inicial."
                    } else {
                        "${"%.2f".format(anterior)} m³ · ${state.fechaAnteriorMillis?.let(DateUtils::format).orEmpty()}"
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        OutlinedButton(
            onClick = { showPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fecha: ${DateUtils.format(state.fechaMillis)}")
        }
        if (state.errorFecha != null) {
            Text(state.errorFecha ?: "", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = state.lecturaTexto,
            onValueChange = viewModel::onLecturaChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Lectura actual (m³)") },
            supportingText = {
                Text(state.errorLectura ?: "Ejemplo: 128.45")
            },
            isError = state.errorLectura != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        if (state.mensajeExito != null) {
            Text(state.mensajeExito ?: "", color = Aqua)
        }
        if (state.avisoIa != null) {
            Text(state.avisoIa ?: "", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = viewModel::guardar,
            enabled = !state.guardando && state.lecturaTexto.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.guardando) "Guardando…" else "Guardar lectura")
        }
        Spacer(Modifier.height(8.dp))
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = state.fechaMillis)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let(viewModel::onFechaChange)
                        showPicker = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
