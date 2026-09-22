package com.tivora.aquahogar.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivora.aquahogar.core.result.AppResult
import com.tivora.aquahogar.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val esRegistro: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val usuarioUid: StateFlow<String?> = authRepository.usuarioUid.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = authRepository.uidActual()
    )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun toggleModo() {
        _uiState.update { it.copy(esRegistro = !it.esRegistro, error = null) }
    }

    fun enviar() {
        val current = _uiState.value
        if (current.cargando) return
        if (current.email.isBlank() || current.password.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa correo y contraseña.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            val result = if (current.esRegistro) {
                authRepository.registrar(current.email, current.password)
            } else {
                authRepository.iniciarSesion(current.email, current.password)
            }
            _uiState.update {
                when (result) {
                    is AppResult.Error -> it.copy(cargando = false, error = result.message)
                    is AppResult.Success -> it.copy(cargando = false, password = "")
                }
            }
        }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
    }
}
