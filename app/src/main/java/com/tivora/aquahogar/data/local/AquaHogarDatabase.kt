package com.tivora.aquahogar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tivora.aquahogar.data.local.dao.AlertaConsumoDao
import com.tivora.aquahogar.data.local.dao.RegistroConsumoDao
import com.tivora.aquahogar.data.local.dao.ViviendaDao
import com.tivora.aquahogar.data.local.entity.AlertaConsumoEntity
import com.tivora.aquahogar.data.local.entity.RegistroConsumoEntity
import com.tivora.aquahogar.data.local.entity.ViviendaEntity

@Database(
    entities = [
        ViviendaEntity::class,
        RegistroConsumoEntity::class,
        AlertaConsumoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AquaHogarDatabase : RoomDatabase() {
    abstract fun viviendaDao(): ViviendaDao
    abstract fun registroConsumoDao(): RegistroConsumoDao
    abstract fun alertaConsumoDao(): AlertaConsumoDao
}
