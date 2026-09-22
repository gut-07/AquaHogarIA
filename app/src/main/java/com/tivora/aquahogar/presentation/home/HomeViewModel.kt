package com.tivora.aquahogar.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivora.aquahogar.domain.model.ResumenHome
import com.tivora.aquahogar.domain.repository.AuthRepository
import com.tivora.aquahogar.domain.usecase.ObtenerResumenHomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val cargando: Boolean = true,
    val resumen: ResumenHome? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    obtenerResumen: ObtenerResumenHomeUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = obtenerResumen()
        .map { HomeUiState(cargando = false, resumen = it) }
        .catch { emit(HomeUiState(cargando = false, error = it.message ?: "No se pudo cargar el inicio")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun cerrarSesion() {
        authRepository.cerrarSesion()
    }
}
