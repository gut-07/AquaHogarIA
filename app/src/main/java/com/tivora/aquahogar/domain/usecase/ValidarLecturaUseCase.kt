package com.tivora.aquahogar.domain.usecase

import com.tivora.aquahogar.core.result.AppResult
import com.tivora.aquahogar.domain.model.RegistroConsumo

sealed class LecturaValidation {
    data object Ok : LecturaValidation()
    data class Error(val message: String) : LecturaValidation()
}

object ValidarLecturaUseCase {

    fun validar(
        lecturaTexto: String,
        lecturaAnteriorM3: Double?,
        fechaMillis: Long,
        fechaAnteriorMillis: Long?
    ): LecturaValidation {
        val lectura = lecturaTexto.replace(",", ".").toDoubleOrNull()
            ?: return LecturaValidation.Error("Ingresa una lectura numérica válida en m³")

        if (lectura < 0) {
            return LecturaValidation.Error("La lectura del medidor no puede ser negativa")
        }

        if (lecturaAnteriorM3 != null && lectura < lecturaAnteriorM3) {
            return LecturaValidation.Error(
                "La lectura no puede ser menor que la anterior (${formatM3(lecturaAnteriorM3)} m³)"
            )
        }

        if (fechaAnteriorMillis != null && fechaMillis < fechaAnteriorMillis) {
            return LecturaValidation.Error("La fecha no puede ser anterior a la última lectura registrada")
        }

        return LecturaValidation.Ok
    }

    fun parseLectura(lecturaTexto: String): AppResult<Double> {
        val lectura = lecturaTexto.replace(",", ".").toDoubleOrNull()
            ?: return AppResult.Error("Ingresa una lectura numérica válida en m³")
        return AppResult.Success(lectura)
    }

    fun puedeRegistrar(registros: List<RegistroConsumo>, nuevaLectura: Double, nuevaFecha: Long): LecturaValidation {
        val anterior = registros
            .filter { it.fecha <= nuevaFecha }
            .maxWithOrNull(compareBy<RegistroConsumo> { it.fecha }.thenBy { it.id })
        return validar(
            lecturaTexto = nuevaLectura.toString(),
            lecturaAnteriorM3 = anterior?.lecturaMedidorM3,
            fechaMillis = nuevaFecha,
            fechaAnteriorMillis = anterior?.fecha
        )
    }

    private fun formatM3(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString() else "%.2f".format(value)
}
