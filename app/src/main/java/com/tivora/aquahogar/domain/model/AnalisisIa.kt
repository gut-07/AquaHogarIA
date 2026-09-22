package com.tivora.aquahogar.domain.model

data class AnalisisIa(
    val nivel: NivelAlerta,
    val explicacion: String,
    val recomendaciones: List<String>,
    val advertencia: String
) {
    fun toDescripcion(): String = buildString {
        append(explicacion.trim())
        if (recomendaciones.isNotEmpty()) {
            append("\n\nRecomendaciones:")
            recomendaciones.forEach { append("\n• ").append(it) }
        }
        if (advertencia.isNotBlank()) {
            append("\n\n").append(advertencia.trim())
        }
    }
}
