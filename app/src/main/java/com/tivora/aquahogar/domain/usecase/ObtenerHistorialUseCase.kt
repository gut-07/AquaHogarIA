package com.tivora.aquahogar.domain.usecase

import com.tivora.aquahogar.core.time.DateUtils
import com.tivora.aquahogar.domain.model.ConsumoCalculado
import com.tivora.aquahogar.domain.model.PeriodoHistorial
import com.tivora.aquahogar.domain.model.PuntoHistorial
import com.tivora.aquahogar.domain.repository.ConsumoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObtenerHistorialUseCase @Inject constructor(
    private val ensureVivienda: EnsureViviendaUseCase,
    private val repository: ConsumoRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(periodo: PeriodoHistorial): Flow<List<PuntoHistorial>> = flow {
        emit(ensureVivienda())
    }.flatMapLatest { vivienda ->
        repository.observeConsumosCalculados(vivienda.id).map { consumos ->
            agrupar(consumos, periodo)
        }
    }

    private fun agrupar(
        consumos: List<ConsumoCalculado>,
        periodo: PeriodoHistorial
    ): List<PuntoHistorial> {
        val grupos = when (periodo) {
            PeriodoHistorial.DIARIO -> consumos.groupBy { DateUtils.toLocalDate(it.fecha).toString() }
            PeriodoHistorial.SEMANAL -> consumos.groupBy { DateUtils.weekKey(DateUtils.toLocalDate(it.fecha)) }
            PeriodoHistorial.MENSUAL -> consumos.groupBy { DateUtils.monthKey(DateUtils.toLocalDate(it.fecha)) }
        }
        return grupos.map { (clave, items) ->
            val fecha = items.minOf { it.fecha }
            PuntoHistorial(
                etiqueta = when (periodo) {
                    PeriodoHistorial.DIARIO -> DateUtils.formatShort(fecha)
                    PeriodoHistorial.SEMANAL -> clave
                    PeriodoHistorial.MENSUAL -> clave
                },
                litros = items.sumOf { it.consumoLitros },
                fechaInicio = fecha
            )
        }.sortedBy { it.fechaInicio }
    }
}
