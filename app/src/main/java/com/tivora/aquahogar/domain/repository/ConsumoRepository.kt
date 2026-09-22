package com.tivora.aquahogar.domain.repository

import com.tivora.aquahogar.core.result.AppResult
import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.model.AnalisisIa
import com.tivora.aquahogar.domain.model.ConsumoCalculado
import com.tivora.aquahogar.domain.model.RegistroConsumo
import com.tivora.aquahogar.domain.model.Vivienda
import kotlinx.coroutines.flow.Flow

interface ConsumoRepository {
    fun observeVivienda(): Flow<Vivienda?>
    suspend fun getVivienda(): Vivienda?
    suspend fun upsertVivienda(vivienda: Vivienda): Long

    fun observeRegistros(viviendaId: Long): Flow<List<RegistroConsumo>>
    suspend fun getRegistros(viviendaId: Long): List<RegistroConsumo>
    suspend fun getUltimoRegistro(viviendaId: Long): RegistroConsumo?
    suspend fun getRegistroAnterior(viviendaId: Long, fecha: Long): RegistroConsumo?
    suspend fun insertarRegistro(registro: RegistroConsumo): Long

    fun observeConsumosCalculados(viviendaId: Long): Flow<List<ConsumoCalculado>>

    fun observeAlertas(viviendaId: Long): Flow<List<AlertaConsumo>>
    suspend fun insertarAlerta(alerta: AlertaConsumo): Long
    suspend fun actualizarAlerta(alerta: AlertaConsumo)

    suspend fun sincronizarNube()
    suspend fun analizarConsumoIa(): AppResult<AnalisisIa>
    suspend fun preguntarAsistente(mensaje: String): AppResult<String>
}
