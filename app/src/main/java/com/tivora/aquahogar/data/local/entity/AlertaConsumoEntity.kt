package com.tivora.aquahogar.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alertas_consumo",
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
data class AlertaConsumoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val viviendaId: Long,
    val fecha: Long,
    val nivel: String,
    val porcentajeVariacion: Double,
    val descripcion: String
)
