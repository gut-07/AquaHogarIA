package com.tivora.aquahogar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tivora.aquahogar.data.local.entity.AlertaConsumoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaConsumoDao {
    @Query("SELECT * FROM alertas_consumo WHERE viviendaId = :viviendaId ORDER BY fecha DESC, id DESC")
    fun observeByVivienda(viviendaId: Long): Flow<List<AlertaConsumoEntity>>

    @Query("SELECT * FROM alertas_consumo WHERE viviendaId = :viviendaId ORDER BY fecha DESC, id DESC")
    suspend fun getByVivienda(viviendaId: Long): List<AlertaConsumoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alerta: AlertaConsumoEntity): Long

    @Update
    suspend fun update(alerta: AlertaConsumoEntity)
}
