package com.tivora.aquahogar.domain.model

enum class EstadoMeta {
    DENTRO,
    CERCA,
    EXCEDIDA
}

data class ResumenHome(
    val vivienda: Vivienda,
    val consumoHoyLitros: Double,
    val acumuladoMensualLitros: Double,
    val promedioDiarioLitros: Double,
    val porcentajeMeta: Double,
    val estadoMeta: EstadoMeta,
    val hayLecturas: Boolean
)
