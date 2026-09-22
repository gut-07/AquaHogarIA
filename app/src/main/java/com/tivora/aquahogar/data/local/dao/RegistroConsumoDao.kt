package com.tivora.aquahogar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tivora.aquahogar.data.local.entity.RegistroConsumoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroConsumoDao {
    @Query("SELECT * FROM registros_consumo WHERE viviendaId = :viviendaId ORDER BY fecha ASC")
    fun observeByVivienda(viviendaId: Long): Flow<List<RegistroConsumoEntity>>

    @Query("SELECT * FROM registros_consumo WHERE viviendaId = :viviendaId ORDER BY fecha ASC")
    suspend fun getByVivienda(viviendaId: Long): List<RegistroConsumoEntity>

    @Query(
        "SELECT * FROM registros_consumo WHERE viviendaId = :viviendaId ORDER BY fecha DESC, id DESC LIMIT 1"
    )
    suspend fun getUltimo(viviendaId: Long): RegistroConsumoEntity?

    @Query(
        """
        SELECT * FROM registros_consumo
        WHERE viviendaId = :viviendaId AND fecha <= :fecha
        ORDER BY fecha DESC, id DESC
        LIMIT 1
        """
    )
    suspend fun getAnteriorA(viviendaId: Long, fecha: Long): RegistroConsumoEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(registro: RegistroConsumoEntity): Long
}
