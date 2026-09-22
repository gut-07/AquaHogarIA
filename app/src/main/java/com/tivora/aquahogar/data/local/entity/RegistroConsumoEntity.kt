package com.tivora.aquahogar.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registros_consumo",
    foreignKeys = [
        ForeignKey(
            entity = ViviendaEntity::class,
            parentColumns = ["id"],
            childColumns = ["viviendaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("viviendaId"), Index("fecha")]
)
data class RegistroConsumoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val viviendaId: Long,
    val fecha: Long,
    val lecturaMedidorM3: Double
)
