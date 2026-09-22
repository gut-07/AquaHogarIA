package com.tivora.aquahogar.data.remote.network

import com.google.gson.Gson
import com.tivora.aquahogar.core.result.AppResult
import com.tivora.aquahogar.data.remote.dto.ApiErrorDto
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object IaErrorMapper {
    private val gson = Gson()

    fun from(error: Throwable): AppResult.Error {
        val http = error as? HttpException
        val body = http?.response()?.errorBody()?.string()
        val parsed = body?.let {
            runCatching { gson.fromJson(it, ApiErrorDto::class.java) }.getOrNull()
        }
        val code = parsed?.code
        val message = when {
            code == "IA_TIMEOUT" || error is SocketTimeoutException ->
                "El análisis inteligente no respondió a tiempo."
            code == "IA_UNAVAILABLE" ->
                "El servicio de IA no está disponible. Inténtalo más tarde."
            code == "IA_INVALID_RESPONSE" ->
                "El análisis inteligente devolvió una respuesta inválida."
            code == "UNAUTHENTICATED" || http?.code() == 401 ->
                "Inicia sesión para usar el asistente en la nube."
            error is IOException ->
                "Sin conexión. Se usará la detección local."
            parsed != null && !parsed.message.isNullOrBlank() -> parsed.message.orEmpty()
            else -> "No se pudo completar el análisis inteligente."
        }
        return AppResult.Error(message)
    }
}
