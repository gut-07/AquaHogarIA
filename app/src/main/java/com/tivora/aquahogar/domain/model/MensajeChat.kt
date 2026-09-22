package com.tivora.aquahogar.domain.model

data class MensajeChat(
    val id: String,
    val texto: String,
    val esUsuario: Boolean,
    val fecha: Long = System.currentTimeMillis()
)
