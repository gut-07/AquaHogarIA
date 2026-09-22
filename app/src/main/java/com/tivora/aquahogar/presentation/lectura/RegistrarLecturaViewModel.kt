package com.tivora.aquahogar.presentation.lectura

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivora.aquahogar.core.result.AppResult
import com.tivora.aquahogar.core.time.DateUtils
import com.tivora.aquahogar.domain.repository.ConsumoRepository
import com.tivora.aquahogar.domain.usecase.EnsureViviendaUseCase
import com.tivora.aquahogar.domain.usecase.LecturaValidation
import com.tivora.aquahogar.domain.usecase.RegistrarLecturaUseCase
import com.tivora.aquahogar.domain.usecase.ValidarLecturaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrarLecturaUiState(
    val viviendaId: Long = 0,
    val fechaMillis: Long = System.currentTimeMillis(),
    val lecturaTexto: String = "",
    val lecturaAnteriorM3: Double? = null,
    val fechaAnteriorMillis: Long? = null,
    val errorLectura: String? = null,
    val errorFecha: String? = null,
    val guardando: Boolean = false,
    val mensajeExito: String? = null,
    val avisoIa: String? = null
)

@HiltViewModel
class RegistrarLecturaViewModel @Inject constructor(
    private val ensureVivienda: EnsureViviendaUseCase,
    private val registrarLectura: RegistrarLecturaUseCase,
    private val repository: ConsumoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrarLecturaUiState())
    val uiState: StateFlow<RegistrarLecturaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val vivienda = ensureVivienda()
            repository.observeRegistros(vivienda.id).collect { registros ->
                val ultimo = registros.maxWithOrNull(
                    compareBy<com.tivora.aquahogar.domain.model.RegistroConsumo> { it.fecha }
                        .thenBy { it.id }
                )
                _uiState.update {
                    it.copy(
                        viviendaId = vivienda.id,
                        lecturaAnteriorM3 = ultimo?.lecturaMedidorM3,
                        fechaAnteriorMillis = ultimo?.fecha
                    )
                }
            }
        }
    }

    fun onLecturaChange(value: String) {
        _uiState.update {
            it.copy(
                lecturaTexto = value.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' },
                errorLectura = null,
                mensajeExito = null,
                avisoIa = null
            )
        }
    }

    fun onFechaChange(millis: Long) {
        _uiState.update { it.copy(fechaMillis = millis, errorFecha = null, mensajeExito = null, avisoIa = null) }
    }

    fun guardar() {
        val current = _uiState.value
        val validacion = ValidarLecturaUseCase.validar(
            lecturaTexto = current.lecturaTexto,
            lecturaAnteriorM3 = current.lecturaAnteriorM3,
            fechaMillis = current.fechaMillis,
            fechaAnteriorMillis = current.fechaAnteriorMillis
        )
        if (validacion is LecturaValidation.Error) {
            val esFecha = validacion.message.contains("fecha", ignoreCase = true)
            _uiState.update {
                it.copy(
                    errorLectura = if (esFecha) null else validacion.message,
                    errorFecha = if (esFecha) validacion.message else null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, errorLectura = null, errorFecha = null) }
            when (
                val result = registrarLectura(
                    viviendaId = current.viviendaId,
                    fechaMillis = current.fechaMillis,
                    lecturaTexto = current.lecturaTexto
                )
            ) {
                is AppResult.Error -> _uiState.update {
                    it.copy(guardando = false, errorLectura = result.message)
                }
                is AppResult.Success -> {
                    val consumo = result.data.consumo
                    val mensaje = if (consumo == null) {
                        "Primera lectura guardada. El consumo se calculará con la siguiente."
                    } else {
                        "Lectura guardada. Consumo: ${"%.0f".format(consumo.consumoLitros)} L " +
                            "(${DateUtils.format(consumo.fecha)})."
                    }
                    _uiState.update {
                        it.copy(
                            guardando = false,
                            lecturaTexto = "",
                            mensajeExito = mensaje,
                            avisoIa = result.data.avisoIa
                        )
                    }
                }
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeExito = null, avisoIa = null) }
    }
}
