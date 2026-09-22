package com.tivora.aquahogar.domain.usecase

/**
 * Reglas de negocio de consumo (RF-05, RF-08).
 *
 * C_i = (L_i - L_{i-1}) * 1000  → litros desde m³
 * V   = ((C - P) / P) * 100     → variación porcentual
 */
object CalculoConsumo {
    const val LITROS_POR_M3 = 1_000.0
    const val UMBRAL_PRECAUCION = 30.0
    const val UMBRAL_ALTO = 50.0

    fun consumoLitros(lecturaActualM3: Double, lecturaAnteriorM3: Double): Double =
        (lecturaActualM3 - lecturaAnteriorM3) * LITROS_POR_M3

    fun variacionPorcentaje(consumo: Double, consumoPrevio: Double): Double {
        if (consumoPrevio <= 0.0) return 0.0
        return ((consumo - consumoPrevio) / consumoPrevio) * 100.0
    }
}
