package com.tivora.aquahogar.presentation.asistente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tivora.aquahogar.domain.model.MensajeChat
import com.tivora.aquahogar.presentation.theme.Aqua
import com.tivora.aquahogar.presentation.theme.DeepWater

@Composable
fun AsistenteScreen(viewModel: AsistenteViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(state.mensajes.size) {
        if (state.mensajes.isNotEmpty()) {
            listState.animateScrollToItem(state.mensajes.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Asistente IA",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.mensajes, key = { it.id }) { mensaje ->
                Burbuja(mensaje)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = state.input,
                onValueChange = viewModel::onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(if (state.enviando) "Consultando al asistente…" else "Escribe una pregunta…") },
                singleLine = true
            )
            IconButton(
                onClick = viewModel::enviar,
                enabled = state.input.isNotBlank() && !state.enviando
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Aqua)
            }
        }
    }
}

@Composable
private fun Burbuja(mensaje: MensajeChat) {
    val bg = if (mensaje.esUsuario) Aqua else MaterialTheme.colorScheme.surface
    val fg = if (mensaje.esUsuario) DeepWater else MaterialTheme.colorScheme.onSurface
    val alignment = if (mensaje.esUsuario) Alignment.End else Alignment.Start
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Text(
            text = mensaje.texto,
            color = fg,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(bg)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}
