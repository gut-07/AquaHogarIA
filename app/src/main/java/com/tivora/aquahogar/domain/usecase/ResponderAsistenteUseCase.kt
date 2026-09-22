package com.tivora.aquahogar.domain.usecase

import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.model.MensajeChat
import com.tivora.aquahogar.domain.model.NivelAlerta
import com.tivora.aquahogar.domain.model.ResumenHome
import java.util.UUID
import javax.inject.Inject

class ResponderAsistenteUseCase @Inject constructor() {

    operator fun invoke(
        pregunta: String,
        resumen: ResumenHome?,
        alertas: List<AlertaConsumo>
    ): MensajeChat {
        val texto = construirRespuesta(pregunta.lowercase(), resumen, alertas)
        return MensajeChat(
            id = UUID.randomUUID().toString(),
            texto = texto,
            esUsuario = false
        )
    }

    private fun construirRespuesta(
        pregunta: String,
        resumen: ResumenHome?,
        alertas: List<AlertaConsumo>
    ): String {
        val ultimaAlerta = alertas.maxByOrNull { it.fecha }
        return when {
            resumen == null ->
                "Aún no hay datos de tu hogar. Registra la primera lectura del medidor para empezar."
            pregunta.contains("fuga") || pregunta.contains("alto") || pregunta.contains("alerta") -> {
                val alerta = ultimaAlerta
                if (alerta == null) {
                    "No hay alertas recientes. Sigue registrando lecturas para detectar incrementos > 30% (precaución) o > 50% (alto)."
                } else {
                    "${alerta.descripcion}\n\n${GenerarAlertaUseCase.recomendacion(alerta.nivel)}"
                }
            }
            pregunta.contains("meta") || pregunta.contains("ahorrar") || pregunta.contains("ahorro") -> {
                val pct = "%.0f".format(resumen.porcentajeMeta)
                "Este mes llevas ${"%.0f".format(resumen.acumuladoMensualLitros)} L " +
                    "(${pct}% de tu meta de ${"%.0f".format(resumen.vivienda.metaMensualLitros)} L). " +
                    GenerarAlertaUseCase.recomendacion(ultimaAlerta?.nivel ?: NivelAlerta.NORMAL)
            }
            pregunta.contains("hoy") || pregunta.contains("consumo") -> {
                "Hoy: ${"%.0f".format(resumen.consumoHoyLitros)} L. " +
                    "Acumulado del mes: ${"%.0f".format(resumen.acumuladoMensualLitros)} L. " +
                    "Promedio diario: ${"%.0f".format(resumen.promedioDiarioLitros)} L."
            }
            else ->
                "Soy el asistente de AquaHogar. Puedo explicarte tu consumo de hoy, el estado de la meta " +
                    "o las alertas por incrementos repentinos. Pregunta, por ejemplo: «¿cómo va mi meta?»"
        }
    }
}
