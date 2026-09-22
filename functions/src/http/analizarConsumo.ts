import {Request, Response} from "express";
import {generateJsonAnalisis} from "../ai/openRouterClient";
import {ANALISIS_SYSTEM_PROMPT, formatAnalisisUserPrompt} from "../ai/prompts";
import {persistAnalisisIa} from "../firestore/viviendaStore";
import {AnalizarConsumoPayload, ApiError} from "../types";
import {sendError} from "./errors";

function asNumber(value: unknown, field: string): number {
  const n = typeof value === "number" ? value : Number(value);
  if (!Number.isFinite(n)) {
    throw new ApiError(400, "INVALID_ARGUMENT", `El campo ${field} debe ser numérico.`);
  }
  return n;
}

function parsePayload(body: unknown): AnalizarConsumoPayload {
  if (!body || typeof body !== "object") {
    throw new ApiError(400, "INVALID_ARGUMENT", "El cuerpo debe ser un JSON de contexto.");
  }
  const data = body as Record<string, unknown>;
  return {
    habitantes: asNumber(data.habitantes, "habitantes"),
    consumo_hoy_litros: asNumber(data.consumo_hoy_litros, "consumo_hoy_litros"),
    promedio_7_dias_litros: asNumber(data.promedio_7_dias_litros, "promedio_7_dias_litros"),
    variacion_porcentaje: asNumber(data.variacion_porcentaje, "variacion_porcentaje"),
    meta_mensual_litros: asNumber(data.meta_mensual_litros, "meta_mensual_litros"),
    consumo_acumulado_litros: asNumber(data.consumo_acumulado_litros, "consumo_acumulado_litros"),
    dias_transcurridos: asNumber(data.dias_transcurridos, "dias_transcurridos"),
    patron_detectado: String(data.patron_detectado ?? "desconocido").slice(0, 80),
  };
}

export async function analizarConsumo(
  req: Request,
  res: Response,
  apiKey: string
): Promise<void> {
  try {
    const payload = parsePayload(req.body);
    const analisis = await generateJsonAnalisis(
      apiKey,
      ANALISIS_SYSTEM_PROMPT,
      formatAnalisisUserPrompt(JSON.stringify(payload))
    );

    persistAnalisisIa(req.uid, payload.variacion_porcentaje, analisis).catch((err) => {
      console.warn("No se pudo persistir el análisis IA", err);
    });

    res.status(200).json(analisis);
  } catch (err) {
    sendError(res, err);
  }
}
