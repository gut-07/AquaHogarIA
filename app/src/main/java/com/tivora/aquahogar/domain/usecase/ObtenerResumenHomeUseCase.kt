package com.tivora.aquahogar.domain.usecase

import com.tivora.aquahogar.core.time.DateUtils
import com.tivora.aquahogar.domain.model.ConsumoCalculado
import com.tivora.aquahogar.domain.model.EstadoMeta
import com.tivora.aquahogar.domain.model.ResumenHome
import com.tivora.aquahogar.domain.model.Vivienda
import com.tivora.aquahogar.domain.repository.ConsumoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.min

class ObtenerResumenHomeUseCase @Inject constructor(
    private val ensureVivienda: EnsureViviendaUseCase,
    private val repository: ConsumoRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<ResumenHome> = flow {
        emit(ensureVivienda())
    }.flatMapLatest { vivienda ->
        repository.observeConsumosCalculados(vivienda.id).map { consumos ->
            buildResumen(vivienda, consumos)
        }
    }

    private fun buildResumen(vivienda: Vivienda, consumos: List<ConsumoCalculado>): ResumenHome {
        val hoy = DateUtils.today()
        val mes = DateUtils.currentYearMonth()
        val consumoHoy = consumos
            .filter { DateUtils.toLocalDate(it.fecha) == hoy }
            .sumOf { it.consumoLitros }
        val delMes = consumos.filter {
            val date = DateUtils.toLocalDate(it.fecha)
            date.year == mes.year && date.month == mes.month
        }
        val acumulado = delMes.sumOf { it.consumoLitros }
        val diasConDato = delMes.map { DateUtils.toLocalDate(it.fecha) }.distinct().size
        val promedio = if (diasConDato == 0) 0.0 else acumulado / diasConDato
        val porcentaje = if (vivienda.metaMensualLitros <= 0) {
            0.0
        } else {
            min(999.0, (acumulado / vivienda.metaMensualLitros) * 100.0)
        }
        val estado = when {
            porcentaje >= 100 -> EstadoMeta.EXCEDIDA
            porcentaje >= 80 -> EstadoMeta.CERCA
            else -> EstadoMeta.DENTRO
        }
        return ResumenHome(
            vivienda = vivienda,
            consumoHoyLitros = consumoHoy,
            acumuladoMensualLitros = acumulado,
            promedioDiarioLitros = promedio,
            porcentajeMeta = porcentaje,
            estadoMeta = estado,
            hayLecturas = consumos.isNotEmpty()
        )
    }
}
