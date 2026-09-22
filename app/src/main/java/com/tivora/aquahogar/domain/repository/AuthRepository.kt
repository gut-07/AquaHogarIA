package com.tivora.aquahogar.domain.repository

import com.tivora.aquahogar.core.result.AppResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val usuarioUid: Flow<String?>
    fun uidActual(): String?
    suspend fun iniciarSesion(email: String, password: String): AppResult<Unit>
    suspend fun registrar(email: String, password: String): AppResult<Unit>
    fun cerrarSesion()
}
