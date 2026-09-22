package com.tivora.aquahogar.presentation.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivora.aquahogar.domain.model.PeriodoHistorial
import com.tivora.aquahogar.domain.model.PuntoHistorial
import com.tivora.aquahogar.domain.usecase.ObtenerHistorialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HistorialUiState(
    val periodo: PeriodoHistorial = PeriodoHistorial.DIARIO,
    val puntos: List<PuntoHistorial> = emptyList()
)

@HiltViewModel
class HistorialViewModel @Inject constructor(
    obtenerHistorial: ObtenerHistorialUseCase
) : ViewModel() {

    private val periodo = MutableStateFlow(PeriodoHistorial.DIARIO)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HistorialUiState> = periodo
        .flatMapLatest { selected ->
            obtenerHistorial(selected).map { HistorialUiState(periodo = selected, puntos = it) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistorialUiState()
        )

    fun onPeriodoChange(value: PeriodoHistorial) {
        periodo.value = value
    }
}
