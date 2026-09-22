import {Request, Response} from "express";
import {generateChatReply} from "../ai/openRouterClient";
import {CHAT_SYSTEM_PROMPT, formatChatUserPrompt} from "../ai/prompts";
import {loadViviendaContext} from "../firestore/viviendaStore";
import {ApiError} from "../types";
import {sendError} from "./errors";

export async function chatAsistente(
  req: Request,
  res: Response,
  apiKey: string
): Promise<void> {
  try {
    const mensaje = String(req.body?.mensaje ?? "").trim();
    if (!mensaje) {
      throw new ApiError(400, "INVALID_ARGUMENT", "Escribe una pregunta para el asistente.");
    }
    if (mensaje.length > 500) {
      throw new ApiError(400, "INVALID_ARGUMENT", "La pregunta es demasiado larga.");
    }

    const contexto = await loadViviendaContext(req.uid);
    const respuesta = await generateChatReply(
      apiKey,
      CHAT_SYSTEM_PROMPT,
      formatChatUserPrompt(JSON.stringify(contexto), mensaje)
    );
    res.status(200).json({respuesta});
  } catch (err) {
    sendError(res, err);
  }
}
