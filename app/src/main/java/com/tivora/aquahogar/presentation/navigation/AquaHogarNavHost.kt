package com.tivora.aquahogar.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tivora.aquahogar.presentation.alertas.AlertasScreen
import com.tivora.aquahogar.presentation.asistente.AsistenteScreen
import com.tivora.aquahogar.presentation.auth.AuthViewModel
import com.tivora.aquahogar.presentation.auth.LoginScreen
import com.tivora.aquahogar.presentation.historial.HistorialScreen
import com.tivora.aquahogar.presentation.home.HomeScreen
import com.tivora.aquahogar.presentation.lectura.LecturaScreen

@Composable
fun AquaHogarNavHost(authViewModel: AuthViewModel = hiltViewModel()) {
    val uid by authViewModel.usuarioUid.collectAsStateWithLifecycle()
    if (uid == null) {
        LoginScreen(viewModel = authViewModel)
        return
    }

    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(tonalElevation = 0.dp) {
                Destino.items.forEach { destino ->
                    val selected = current?.hierarchy?.any { it.route == destino.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destino.route) {
                                popUpTo(Destino.Inicio.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destino.icon, contentDescription = destino.label) },
                        label = { Text(destino.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destino.Inicio.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destino.Inicio.route) { HomeScreen() }
            composable(Destino.Lectura.route) { LecturaScreen() }
            composable(Destino.Historial.route) { HistorialScreen() }
            composable(Destino.Alertas.route) { AlertasScreen() }
            composable(Destino.Asistente.route) { AsistenteScreen() }
        }
    }
}
