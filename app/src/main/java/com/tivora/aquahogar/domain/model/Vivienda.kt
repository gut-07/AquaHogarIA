package com.tivora.aquahogar.domain.model

data class Vivienda(
    val id: Long = 0,
    val nombre: String,
    val numeroHabitantes: Int,
    val metaMensualLitros: Double
)
