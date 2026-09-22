package com.tivora.aquahogar.domain.usecase

import com.tivora.aquahogar.core.result.AppResult
import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.model.ConsumoCalculado
import com.tivora.aquahogar.domain.model.RegistroConsumo
import com.tivora.aquahogar.domain.repository.ConsumoRepository
import javax.inject.Inject

data class LecturaRegistrada(
    val registro: RegistroConsumo,
    val consumo: ConsumoCalculado?,
    val avisoIa: String? = null
)

class RegistrarLecturaUseCase @Inject constructor(
    private val repository: ConsumoRepository
) {
    suspend operator fun invoke(
        viviendaId: Long,
        fechaMillis: Long,
        lecturaTexto: String
    ): AppResult<LecturaRegistrada> {
        val ultimo = repository.getUltimoRegistro(viviendaId)
        val validacion = ValidarLecturaUseCase.validar(
            lecturaTexto = lecturaTexto,
            lecturaAnteriorM3 = ultimo?.lecturaMedidorM3,
            fechaMillis = fechaMillis,
            fechaAnteriorMillis = ultimo?.fecha
        )
        if (validacion is LecturaValidation.Error) {
            return AppResult.Error(validacion.message)
        }

        val lecturaM3 = lecturaTexto.replace(",", ".").toDouble()
        val registro = RegistroConsumo(
            viviendaId = viviendaId,
            fecha = fechaMillis,
            lecturaMedidorM3 = lecturaM3
        )
        val id = repository.insertarRegistro(registro)
        val guardado = registro.copy(id = id)

        val consumo = if (ultimo != null) {
            val litros = CalculoConsumo.consumoLitros(lecturaM3, ultimo.lecturaMedidorM3)
            val previoConsumo = consumoPrevio(viviendaId, ultimo)
            val variacion = CalculoConsumo.variacionPorcentaje(litros, previoConsumo)
            val alerta = GenerarAlertaUseCase.fromVariacion(viviendaId, fechaMillis, variacion)
            val alertaId = repository.insertarAlerta(alerta)
            val guardada = alerta.copy(id = alertaId)
            val avisoIa = enriquecerConIa(guardada)
            ConsumoCalculado(fecha = fechaMillis, consumoLitros = litros) to avisoIa
        } else {
            null to null
        }

        return AppResult.Success(
            LecturaRegistrada(guardado, consumo.first, consumo.second)
        )
    }

    private suspend fun enriquecerConIa(alerta: AlertaConsumo): String? {
        return when (val result = repository.analizarConsumoIa()) {
            is AppResult.Error -> result.message
            is AppResult.Success -> {
                val enriquecida = alerta.copy(
                    nivel = result.data.nivel,
                    descripcion = result.data.toDescripcion()
                )
                repository.actualizarAlerta(enriquecida)
                null
            }
        }
    }

    private suspend fun consumoPrevio(viviendaId: Long, ultimo: RegistroConsumo): Double {
        val registros = repository.getRegistros(viviendaId).sortedWith(
            compareBy<RegistroConsumo> { it.fecha }.thenBy { it.id }
        )
        val index = registros.indexOfFirst { it.id == ultimo.id }
        if (index <= 0) return 0.0
        val anterior = registros[index - 1]
        return CalculoConsumo.consumoLitros(ultimo.lecturaMedidorM3, anterior.lecturaMedidorM3)
    }
}
