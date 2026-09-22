package com.tivora.aquahogar.data.repository

import com.tivora.aquahogar.core.result.AppResult
import com.tivora.aquahogar.data.local.dao.AlertaConsumoDao
import com.tivora.aquahogar.data.local.dao.RegistroConsumoDao
import com.tivora.aquahogar.data.local.dao.ViviendaDao
import com.tivora.aquahogar.data.local.mapper.toDomain
import com.tivora.aquahogar.data.local.mapper.toEntity
import com.tivora.aquahogar.data.remote.ContextoIaFactory
import com.tivora.aquahogar.data.remote.datasource.RemoteDataSource
import com.tivora.aquahogar.data.remote.dto.ChatRequestDto
import com.tivora.aquahogar.data.remote.firestore.FirestoreDataSource
import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.model.AnalisisIa
import com.tivora.aquahogar.domain.model.ConsumoCalculado
import com.tivora.aquahogar.domain.model.NivelAlerta
import com.tivora.aquahogar.domain.model.RegistroConsumo
import com.tivora.aquahogar.domain.model.Vivienda
import com.tivora.aquahogar.domain.repository.ConsumoRepository
import com.tivora.aquahogar.domain.usecase.CalculoConsumo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsumoRepositoryImpl @Inject constructor(
    private val viviendaDao: ViviendaDao,
    private val registroDao: RegistroConsumoDao,
    private val alertaDao: AlertaConsumoDao,
    private val firestore: FirestoreDataSource,
    private val remote: RemoteDataSource
) : ConsumoRepository {

    override fun observeVivienda(): Flow<Vivienda?> =
        viviendaDao.observePrimera().map { it?.toDomain() }

    override suspend fun getVivienda(): Vivienda? = viviendaDao.getPrimera()?.toDomain()

    override suspend fun upsertVivienda(vivienda: Vivienda): Long {
        val id = if (vivienda.id == 0L) {
            viviendaDao.insert(vivienda.toEntity())
        } else {
            viviendaDao.update(vivienda.toEntity())
            vivienda.id
        }
        runCatching { firestore.upsertVivienda(vivienda.copy(id = id)) }
        return id
    }

    override fun observeRegistros(viviendaId: Long): Flow<List<RegistroConsumo>> =
        registroDao.observeByVivienda(viviendaId).map { list -> list.map { it.toDomain() } }

    override suspend fun getRegistros(viviendaId: Long): List<RegistroConsumo> =
        registroDao.getByVivienda(viviendaId).map { it.toDomain() }

    override suspend fun getUltimoRegistro(viviendaId: Long): RegistroConsumo? =
        registroDao.getUltimo(viviendaId)?.toDomain()

    override suspend fun getRegistroAnterior(viviendaId: Long, fecha: Long): RegistroConsumo? =
        registroDao.getAnteriorA(viviendaId, fecha)?.toDomain()

    override suspend fun insertarRegistro(registro: RegistroConsumo): Long {
        val id = registroDao.insert(registro.toEntity())
        runCatching { firestore.upsertRegistro(registro.copy(id = id)) }
        return id
    }

    override fun observeConsumosCalculados(viviendaId: Long): Flow<List<ConsumoCalculado>> =
        observeRegistros(viviendaId).map { registros -> calcularConsumos(registros) }

    override fun observeAlertas(viviendaId: Long): Flow<List<AlertaConsumo>> =
        alertaDao.observeByVivienda(viviendaId).map { list -> list.map { it.toDomain() } }

    override suspend fun insertarAlerta(alerta: AlertaConsumo): Long {
        val id = alertaDao.insert(alerta.toEntity())
        runCatching { firestore.upsertAlerta(alerta.copy(id = id), fuente = "local") }
        return id
    }

    override suspend fun actualizarAlerta(alerta: AlertaConsumo) {
        alertaDao.update(alerta.toEntity())
        runCatching { firestore.upsertAlerta(alerta, fuente = "ia") }
    }

    override suspend fun sincronizarNube() {
        val vivienda = getVivienda() ?: return
        runCatching { firestore.upsertVivienda(vivienda) }
        getRegistros(vivienda.id).forEach { registro ->
            runCatching { firestore.upsertRegistro(registro) }
        }
        alertaDao.getByVivienda(vivienda.id).map { it.toDomain() }.forEach { alerta ->
            runCatching { firestore.upsertAlerta(alerta, fuente = "local") }
        }
    }

    override suspend fun analizarConsumoIa(): AppResult<AnalisisIa> {
        val vivienda = getVivienda() ?: return AppResult.Error("No hay vivienda registrada.")
        val request = ContextoIaFactory.from(vivienda, getRegistros(vivienda.id))
            ?: return AppResult.Error("Se necesitan al menos dos lecturas.")
        return when (val result = remote.analizarConsumoIA(request)) {
            is AppResult.Error -> result
            is AppResult.Success -> AppResult.Success(result.data.toAnalisisDomain())
        }
    }

    override suspend fun preguntarAsistente(mensaje: String): AppResult<String> {
        val vivienda = getVivienda() ?: return AppResult.Error("No hay vivienda registrada.")
        runCatching { sincronizarNube() }
        return when (val result = remote.enviarMensaje(ChatRequestDto(vivienda.id, mensaje))) {
            is AppResult.Error -> result
            is AppResult.Success -> AppResult.Success(result.data.respuesta)
        }
    }

    private fun calcularConsumos(registros: List<RegistroConsumo>): List<ConsumoCalculado> {
        val ordenados = registros.sortedWith(compareBy<RegistroConsumo> { it.fecha }.thenBy { it.id })
        if (ordenados.size < 2) return emptyList()
        return ordenados.zipWithNext { anterior, actual ->
            ConsumoCalculado(
                fecha = actual.fecha,
                consumoLitros = CalculoConsumo.consumoLitros(
                    actual.lecturaMedidorM3,
                    anterior.lecturaMedidorM3
                )
            )
        }
    }
}

private fun com.tivora.aquahogar.data.remote.dto.AnalisisIaDto.toAnalisisDomain(): AnalisisIa {
    val parsedNivel = when (nivel.lowercase()) {
        "alto" -> NivelAlerta.ALTO
        "precaucion", "precaución" -> NivelAlerta.PRECAUCION
        else -> NivelAlerta.NORMAL
    }
    return AnalisisIa(
        nivel = parsedNivel,
        explicacion = explicacion,
        recomendaciones = recomendaciones,
        advertencia = advertencia
    )
}
