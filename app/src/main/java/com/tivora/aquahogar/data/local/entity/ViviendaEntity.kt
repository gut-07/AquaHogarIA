package com.tivora.aquahogar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "viviendas")
data class ViviendaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val numeroHabitantes: Int,
    val metaMensualLitros: Double
)
