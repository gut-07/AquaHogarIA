package com.tivora.aquahogar.core.result

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val message: String) : AppResult<Nothing>()
}
