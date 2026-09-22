import {Response} from "express";
import {ApiError} from "../types";

export function sendError(res: Response, err: unknown): void {
  if (err instanceof ApiError) {
    res.status(err.status).json({code: err.code, message: err.message});
    return;
  }

  const message = err instanceof Error ? err.message : String(err);

  if (message === "IA_TIMEOUT" || /timeout|aborted|deadline/i.test(message)) {
    res.status(504).json({
      code: "IA_TIMEOUT",
      message: "El análisis inteligente no respondió a tiempo.",
    });
    return;
  }

  if (/429|quota|resource-exhausted|rate/i.test(message)) {
    res.status(503).json({
      code: "IA_UNAVAILABLE",
      message: "El servicio de IA no está disponible. Inténtalo más tarde.",
    });
    return;
  }

  console.error("Unhandled error", err);
  res.status(500).json({
    code: "INTERNAL",
    message: "No se pudo completar la solicitud.",
  });
}
