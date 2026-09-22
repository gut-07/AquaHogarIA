package com.tivora.aquahogar.data.remote.api

import com.tivora.aquahogar.data.remote.dto.AlertaConsumoDto
import com.tivora.aquahogar.data.remote.dto.AnalisisIaDto
import com.tivora.aquahogar.data.remote.dto.AnalizarConsumoRequest
import com.tivora.aquahogar.data.remote.dto.ChatRequestDto
import com.tivora.aquahogar.data.remote.dto.ChatResponseDto
import com.tivora.aquahogar.data.remote.dto.RegistroConsumoDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Contrato HTTP hacia Cloud Functions (`api` en us-central1).
 * Todas las rutas requieren Bearer token de Firebase Auth.
 */
interface AquaHogarApi {
    @GET("viviendas/{id}/lecturas")
    suspend fun getLecturas(@Path("id") viviendaId: Long): List<RegistroConsumoDto>

    @POST("viviendas/{id}/lecturas")
    suspend fun postLectura(
        @Path("id") viviendaId: Long,
        @Body body: RegistroConsumoDto
    ): RegistroConsumoDto

    @GET("viviendas/{id}/alertas")
    suspend fun getAlertas(@Path("id") viviendaId: Long): List<AlertaConsumoDto>

    @POST("asistente/chat")
    suspend fun enviarMensaje(@Body body: ChatRequestDto): ChatResponseDto

    @POST("analizarConsumoIA")
    suspend fun analizarConsumoIA(@Body body: AnalizarConsumoRequest): AnalisisIaDto
}
