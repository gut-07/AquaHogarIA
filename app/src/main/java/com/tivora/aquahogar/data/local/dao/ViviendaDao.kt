package com.tivora.aquahogar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tivora.aquahogar.data.local.entity.ViviendaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ViviendaDao {
    @Query("SELECT * FROM viviendas ORDER BY id ASC LIMIT 1")
    fun observePrimera(): Flow<ViviendaEntity?>

    @Query("SELECT * FROM viviendas ORDER BY id ASC LIMIT 1")
    suspend fun getPrimera(): ViviendaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vivienda: ViviendaEntity): Long

    @Update
    suspend fun update(vivienda: ViviendaEntity)
}
