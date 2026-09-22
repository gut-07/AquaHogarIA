package com.tivora.aquahogar.presentation.asistente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivora.aquahogar.core.result.AppResult
import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.model.MensajeChat
import com.tivora.aquahogar.domain.model.ResumenHome
import com.tivora.aquahogar.domain.repository.ConsumoRepository
import com.tivora.aquahogar.domain.usecase.ObtenerAlertasUseCase
import com.tivora.aquahogar.domain.usecase.ObtenerResumenHomeUseCase
import com.tivora.aquahogar.domain.usecase.ResponderAsistenteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AsistenteUiState(
    val mensajes: List<MensajeChat> = listOf(
        MensajeChat(
            id = "welcome",
            texto = "Hola, soy el asistente de AquaHogar. Pregúntame por tu consumo de hoy, la meta mensual o las alertas.",
            esUsuario = false
        )
    ),
    val input: String = "",
    val enviando: Boolean = false
)

@HiltViewModel
class AsistenteViewModel @Inject constructor(
    private val obtenerResumen: ObtenerResumenHomeUseCase,
    private val obtenerAlertas: ObtenerAlertasUseCase,
    private val responder: ResponderAsistenteUseCase,
    private val repository: ConsumoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AsistenteUiState())
    val uiState: StateFlow<AsistenteUiState> = _uiState.asStateFlow()

    private var resumen: ResumenHome? = null
    private var alertas: List<AlertaConsumo> = emptyList()

    init {
        viewModelScope.launch {
            obtenerResumen().collect { resumen = it }
        }
        viewModelScope.launch {
            obtenerAlertas().collect { alertas = it }
        }
    }

    fun onInputChange(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    fun enviar() {
        val texto = _uiState.value.input.trim()
        if (texto.isEmpty() || _uiState.value.enviando) return
        val userMsg = MensajeChat(
            id = UUID.randomUUID().toString(),
            texto = texto,
            esUsuario = true
        )
        _uiState.update {
            it.copy(
                mensajes = it.mensajes + userMsg,
                input = "",
                enviando = true
            )
        }
        viewModelScope.launch {
            val reply = when (val remoto = repository.preguntarAsistente(texto)) {
                is AppResult.Success -> MensajeChat(
                    id = UUID.randomUUID().toString(),
                    texto = remoto.data,
                    esUsuario = false
                )
                is AppResult.Error -> {
                    val local = responder(texto, resumen, alertas)
                    local.copy(
                        texto = "${local.texto}\n\n(${remoto.message} Se usó la respuesta local.)"
                    )
                }
            }
            _uiState.update {
                it.copy(mensajes = it.mensajes + reply, enviando = false)
            }
        }
    }
}
