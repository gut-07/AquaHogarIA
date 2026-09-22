package com.tivora.aquahogar.domain.model

data class RegistroConsumo(
    val id: Long = 0,
    val viviendaId: Long,
    val fecha: Long,
    val lecturaMedidorM3: Double
)
