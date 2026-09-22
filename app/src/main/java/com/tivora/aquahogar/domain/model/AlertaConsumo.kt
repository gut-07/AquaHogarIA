package com.tivora.aquahogar.domain.model

enum class NivelAlerta {
    NORMAL,
    PRECAUCION,
    ALTO
}

data class AlertaConsumo(
    val id: Long = 0,
    val viviendaId: Long = 0,
    val fecha: Long,
    val nivel: NivelAlerta,
    val porcentajeVariacion: Double,
    val descripcion: String
)
