package com.tivora.aquahogar.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.tivora.aquahogar.core.result.AppResult
import com.tivora.aquahogar.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val usuarioUid: Flow<String?> = callbackFlow {
        trySend(auth.currentUser?.uid)
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun uidActual(): String? = auth.currentUser?.uid

    override suspend fun iniciarSesion(email: String, password: String): AppResult<Unit> =
        runAuth { auth.signInWithEmailAndPassword(email.trim(), password).await() }

    override suspend fun registrar(email: String, password: String): AppResult<Unit> =
        runAuth { auth.createUserWithEmailAndPassword(email.trim(), password).await() }

    override fun cerrarSesion() {
        auth.signOut()
    }

    private suspend fun runAuth(block: suspend () -> Unit): AppResult<Unit> = try {
        block()
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Error(mapAuthError(e))
    }

    private fun mapAuthError(error: Exception): String {
        val code = (error as? FirebaseAuthException)?.errorCode.orEmpty()
        return when (code) {
            "ERROR_INVALID_EMAIL" -> "El correo no es válido."
            "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" ->
                "Correo o contraseña incorrectos."
            "ERROR_USER_NOT_FOUND" -> "No hay una cuenta con ese correo."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Ese correo ya está registrado."
            "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres."
            "ERROR_NETWORK_REQUEST_FAILED" -> "Sin conexión. Revisa tu red e inténtalo de nuevo."
            else -> error.message ?: "No se pudo completar la autenticación."
        }
    }
}
