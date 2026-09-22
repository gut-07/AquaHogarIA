package com.tivora.aquahogar.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tivora.aquahogar.presentation.theme.Aqua

@Composable
fun LoginScreen(viewModel: AuthViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("AquaHogar IA", style = MaterialTheme.typography.labelLarge, color = Aqua)
        Spacer(Modifier.height(8.dp))
        Text(
            if (state.esRegistro) "Crea tu cuenta" else "Inicia sesión",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            "Tus lecturas se respaldan en la nube y el asistente usa IA sin exponer claves en el teléfono.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Correo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = viewModel::enviar,
            enabled = !state.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                when {
                    state.cargando -> "Espera…"
                    state.esRegistro -> "Crear cuenta"
                    else -> "Entrar"
                }
            )
        }
        TextButton(onClick = viewModel::toggleModo, modifier = Modifier.fillMaxWidth()) {
            Text(
                if (state.esRegistro) "Ya tengo cuenta" else "Crear una cuenta nueva"
            )
        }
    }
}
