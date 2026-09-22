package com.tivora.aquahogar.data.remote.datasource

import com.tivora.aquahogar.core.result.AppResult
import com.tivora.aquahogar.data.remote.api.AquaHogarApi
import com.tivora.aquahogar.data.remote.dto.AnalisisIaDto
import com.tivora.aquahogar.data.remote.dto.AnalizarConsumoRequest
import com.tivora.aquahogar.data.remote.dto.ChatRequestDto
import com.tivora.aquahogar.data.remote.dto.ChatResponseDto
import com.tivora.aquahogar.data.remote.dto.RegistroConsumoDto
import com.tivora.aquahogar.data.remote.network.IaErrorMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val api: AquaHogarApi
) {
    suspend fun getLecturas(viviendaId: Long) = api.getLecturas(viviendaId)

    suspend fun postLectura(dto: RegistroConsumoDto) = api.postLectura(dto.viviendaId, dto)

    suspend fun getAlertas(viviendaId: Long) = api.getAlertas(viviendaId)

    suspend fun enviarMensaje(request: ChatRequestDto): AppResult<ChatResponseDto> = try {
        AppResult.Success(api.enviarMensaje(request))
    } catch (e: Exception) {
        IaErrorMapper.from(e)
    }

    suspend fun analizarConsumoIA(request: AnalizarConsumoRequest): AppResult<AnalisisIaDto> = try {
        AppResult.Success(api.analizarConsumoIA(request))
    } catch (e: Exception) {
        IaErrorMapper.from(e)
    }
}
