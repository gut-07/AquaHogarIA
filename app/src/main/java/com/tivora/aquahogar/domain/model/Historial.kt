package com.tivora.aquahogar.domain.model

enum class PeriodoHistorial {
    DIARIO,
    SEMANAL,
    MENSUAL
}

data class PuntoHistorial(
    val etiqueta: String,
    val litros: Double,
    val fechaInicio: Long
)
