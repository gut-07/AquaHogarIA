package com.tivora.aquahogar.domain.usecase

import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.model.NivelAlerta

object GenerarAlertaUseCase {

    fun fromVariacion(
        viviendaId: Long,
        fecha: Long,
        variacion: Double
    ): AlertaConsumo {
        val nivel = when {
            variacion > CalculoConsumo.UMBRAL_ALTO -> NivelAlerta.ALTO
            variacion > CalculoConsumo.UMBRAL_PRECAUCION -> NivelAlerta.PRECAUCION
            else -> NivelAlerta.NORMAL
        }
        return AlertaConsumo(
            viviendaId = viviendaId,
            fecha = fecha,
            nivel = nivel,
            porcentajeVariacion = variacion,
            descripcion = descripcion(nivel, variacion)
        )
    }

    fun recomendacion(nivel: NivelAlerta): String = when (nivel) {
        NivelAlerta.ALTO ->
            "Revisa fugas en tuberías, inodoros que corren y grifos abiertos. " +
                "Cierra la llave de paso si el consumo no se detiene y registra una nueva lectura mañana."
        NivelAlerta.PRECAUCION ->
            "Reduce el tiempo de ducha, revisa el riego y evita lavar a media carga. " +
                "Compara esta semana con la anterior para confirmar si el alza se mantiene."
        NivelAlerta.NORMAL ->
            "Mantén hábitos eficientes: cierra el grifo al enjabonarte y reutiliza agua de lluvia para plantas."
    }

    private fun descripcion(nivel: NivelAlerta, variacion: Double): String {
        val signo = if (variacion >= 0) "+" else ""
        val pct = "%.1f".format(variacion)
        return when (nivel) {
            NivelAlerta.ALTO ->
                "Incremento repentino $signo$pct% (mayor al 50%). Posible fuga o uso atípico."
            NivelAlerta.PRECAUCION ->
                "Incremento $signo$pct% (mayor al 30%). Vigila duchas, riego y electrodomésticos."
            NivelAlerta.NORMAL ->
                "Variación $signo$pct%. El consumo se mantiene dentro del patrón habitual."
        }
    }
}
