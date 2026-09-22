package com.tivora.aquahogar.data.local.mapper

import com.tivora.aquahogar.data.local.entity.AlertaConsumoEntity
import com.tivora.aquahogar.data.local.entity.RegistroConsumoEntity
import com.tivora.aquahogar.data.local.entity.ViviendaEntity
import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.model.NivelAlerta
import com.tivora.aquahogar.domain.model.RegistroConsumo
import com.tivora.aquahogar.domain.model.Vivienda

fun ViviendaEntity.toDomain() = Vivienda(
    id = id,
    nombre = nombre,
    numeroHabitantes = numeroHabitantes,
    metaMensualLitros = metaMensualLitros
)

fun Vivienda.toEntity() = ViviendaEntity(
    id = id,
    nombre = nombre,
    numeroHabitantes = numeroHabitantes,
    metaMensualLitros = metaMensualLitros
)

fun RegistroConsumoEntity.toDomain() = RegistroConsumo(
    id = id,
    viviendaId = viviendaId,
    fecha = fecha,
    lecturaMedidorM3 = lecturaMedidorM3
)

fun RegistroConsumo.toEntity() = RegistroConsumoEntity(
    id = id,
    viviendaId = viviendaId,
    fecha = fecha,
    lecturaMedidorM3 = lecturaMedidorM3
)

fun AlertaConsumoEntity.toDomain() = AlertaConsumo(
    id = id,
    viviendaId = viviendaId,
    fecha = fecha,
    nivel = runCatching { NivelAlerta.valueOf(nivel) }.getOrDefault(NivelAlerta.NORMAL),
    porcentajeVariacion = porcentajeVariacion,
    descripcion = descripcion
)

fun AlertaConsumo.toEntity() = AlertaConsumoEntity(
    id = id,
    viviendaId = viviendaId,
    fecha = fecha,
    nivel = nivel.name,
    porcentajeVariacion = porcentajeVariacion,
    descripcion = descripcion
)
