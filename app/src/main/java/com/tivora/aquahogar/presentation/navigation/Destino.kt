package com.tivora.aquahogar.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destino(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Inicio : Destino("inicio", "Inicio", Icons.Filled.Home)
    data object Lectura : Destino("lectura", "Registrar", Icons.Filled.WaterDrop)
    data object Historial : Destino("historial", "Historial", Icons.Filled.BarChart)
    data object Alertas : Destino("alertas", "Alertas", Icons.Filled.Notifications)
    data object Asistente : Destino("asistente", "Asistente", Icons.AutoMirrored.Filled.Chat)

    companion object {
        val items = listOf(Inicio, Lectura, Historial, Alertas, Asistente)
    }
}
