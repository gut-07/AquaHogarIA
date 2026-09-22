package com.tivora.aquahogar.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegistroConsumoDto(
    val id: Long? = null,
    val viviendaId: Long,
    val fecha: Long,
    val lecturaMedidorM3: Double
)

data class AlertaConsumoDto(
    val id: Long? = null,
    val viviendaId: Long,
    val fecha: Long,
    val nivel: String,
    val porcentajeVariacion: Double,
    val descripcion: String
)

data class ChatRequestDto(
    val viviendaId: Long,
    val mensaje: String
)

data class ChatResponseDto(
    val respuesta: String
)

data class AnalizarConsumoRequest(
    @SerializedName("habitantes") val habitantes: Int,
    @SerializedName("consumo_hoy_litros") val consumoHoyLitros: Double,
    @SerializedName("promedio_7_dias_litros") val promedio7DiasLitros: Double,
    @SerializedName("variacion_porcentaje") val variacionPorcentaje: Double,
    @SerializedName("meta_mensual_litros") val metaMensualLitros: Double,
    @SerializedName("consumo_acumulado_litros") val consumoAcumuladoLitros: Double,
    @SerializedName("dias_transcurridos") val diasTranscurridos: Int,
    @SerializedName("patron_detectado") val patronDetectado: String
)

data class AnalisisIaDto(
    val nivel: String,
    val explicacion: String,
    val recomendaciones: List<String> = emptyList(),
    val advertencia: String
)

data class ApiErrorDto(
    val code: String? = null,
    val message: String? = null
)
