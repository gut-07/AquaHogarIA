package com.tivora.aquahogar.data.remote

import com.tivora.aquahogar.core.time.DateUtils
import com.tivora.aquahogar.data.remote.dto.AnalizarConsumoRequest
import com.tivora.aquahogar.domain.model.RegistroConsumo
import com.tivora.aquahogar.domain.model.Vivienda
import com.tivora.aquahogar.domain.usecase.CalculoConsumo
import java.time.temporal.ChronoUnit

object ContextoIaFactory {

    fun from(vivienda: Vivienda, registros: List<RegistroConsumo>): AnalizarConsumoRequest? {
        val ordenados = registros.sortedWith(
            compareBy<RegistroConsumo> { it.fecha }.thenBy { it.id }
        )
        if (ordenados.size < 2) return null

        val consumos = ordenados.zipWithNext { anterior, actual ->
            val litros = CalculoConsumo.consumoLitros(
                actual.lecturaMedidorM3,
                anterior.lecturaMedidorM3
            )
            actual.fecha to litros
        }
        val hoy = DateUtils.today()
        val mes = DateUtils.currentYearMonth()
        val consumoHoy = consumos
            .filter { DateUtils.toLocalDate(it.first) == hoy }
            .sumOf { it.second }
        val hace7 = hoy.minusDays(6)
        val ultimaSemana = consumos.filter {
            val date = DateUtils.toLocalDate(it.first)
            !date.isBefore(hace7) && !date.isAfter(hoy)
        }
        val diasSemana = ultimaSemana.map { DateUtils.toLocalDate(it.first) }.distinct().size
        val promedio7 = if (diasSemana == 0) 0.0 else ultimaSemana.sumOf { it.second } / diasSemana
        val delMes = consumos.filter {
            val date = DateUtils.toLocalDate(it.first)
            date.year == mes.year && date.month == mes.month
        }
        val acumulado = delMes.sumOf { it.second }
        val ultimo = consumos.last().second
        val previo = if (consumos.size >= 2) consumos[consumos.lastIndex - 1].second else 0.0
        val variacion = CalculoConsumo.variacionPorcentaje(ultimo, previo)
        val primerDiaMes = mes.atDay(1)
        val dias = ChronoUnit.DAYS.between(primerDiaMes, hoy).toInt() + 1
        val patron = when {
            variacion > CalculoConsumo.UMBRAL_ALTO -> "incremento_repentino"
            variacion > CalculoConsumo.UMBRAL_PRECAUCION -> "incremento_moderado"
            variacion < -CalculoConsumo.UMBRAL_PRECAUCION -> "descenso"
            else -> "estable"
        }
        return AnalizarConsumoRequest(
            habitantes = vivienda.numeroHabitantes,
            consumoHoyLitros = consumoHoy,
            promedio7DiasLitros = promedio7,
            variacionPorcentaje = variacion,
            metaMensualLitros = vivienda.metaMensualLitros,
            consumoAcumuladoLitros = acumulado,
            diasTranscurridos = dias.coerceAtLeast(1),
            patronDetectado = patron
        )
    }
}
