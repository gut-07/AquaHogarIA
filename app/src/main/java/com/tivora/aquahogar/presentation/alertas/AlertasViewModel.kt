package com.tivora.aquahogar.presentation.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.usecase.ObtenerAlertasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlertasViewModel @Inject constructor(
    obtenerAlertas: ObtenerAlertasUseCase
) : ViewModel() {

    val alertas: StateFlow<List<AlertaConsumo>> = obtenerAlertas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
