import {AnalisisIaResponse, ApiError, NIVELES} from "../types";

const IA_TIMEOUT_MS = 15_000;
const OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
const OPENROUTER_MODEL = "openai/gpt-4o-mini";

interface ChatMessage {
  role: "system" | "user";
  content: string;
}

function extractJson(text: string): unknown {
  const trimmed = text.trim().replace(/^```(?:json)?\s*/i, "").replace(/\s*```$/, "");
  try {
    return JSON.parse(trimmed) as unknown;
  } catch {
    throw new ApiError(
      502,
      "IA_INVALID_RESPONSE",
      "La IA devolvió una respuesta inválida."
    );
  }
}

export function parseAnalisis(raw: unknown): AnalisisIaResponse {
  if (!raw || typeof raw !== "object") {
    throw new ApiError(502, "IA_INVALID_RESPONSE", "La IA devolvió una respuesta inválida.");
  }
  const data = raw as Record<string, unknown>;
  const nivel = String(data.nivel ?? "").toLowerCase();
  const explicacion = String(data.explicacion ?? "").trim();
  const advertencia = String(data.advertencia ?? "").trim();
  const recomendaciones = Array.isArray(data.recomendaciones) ?
    data.recomendaciones.map((item) => String(item).trim()).filter(Boolean) :
    [];

  if (!NIVELES.includes(nivel as AnalisisIaResponse["nivel"])) {
    throw new ApiError(502, "IA_INVALID_RESPONSE", "La IA devolvió un nivel no reconocido.");
  }
  if (!explicacion || !advertencia || recomendaciones.length === 0) {
    throw new ApiError(502, "IA_INVALID_RESPONSE", "La IA omitió campos obligatorios.");
  }

  return {
    nivel: nivel as AnalisisIaResponse["nivel"],
    explicacion: explicacion.slice(0, 2000),
    recomendaciones: recomendaciones.slice(0, 10).map((item) => item.slice(0, 300)),
    advertencia: advertencia.slice(0, 1000),
  };
}

function mapOpenRouterError(status: number, body: string): ApiError {
  if (status === 401 || status === 403) {
    return new ApiError(503, "IA_UNAVAILABLE", "La clave de OpenRouter no es válida.");
  }
  if (status === 402 || status === 429) {
    return new ApiError(
      503,
      "IA_UNAVAILABLE",
      "El servicio de IA no está disponible. Inténtalo más tarde."
    );
  }
  if (status >= 500) {
    return new ApiError(
      503,
      "IA_UNAVAILABLE",
      "No se pudo contactar el servicio de IA."
    );
  }
  console.error("OpenRouter error", status, body);
  return new ApiError(503, "IA_UNAVAILABLE", "No se pudo contactar el servicio de IA.");
}

async function completeChat(
  apiKey: string,
  messages: ChatMessage[],
  jsonMode: boolean
): Promise<string> {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), IA_TIMEOUT_MS);
  try {
    const response = await fetch(OPENROUTER_URL, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${apiKey}`,
        "Content-Type": "application/json",
        "HTTP-Referer": "https://tivora-c2cc2.web.app",
        "X-Title": "AquaHogar IA",
      },
      body: JSON.stringify({
        model: OPENROUTER_MODEL,
        temperature: jsonMode ? 0.4 : 0.5,
        max_tokens: jsonMode ? 600 : 400,
        messages,
        ...(jsonMode ? {response_format: {type: "json_object"}} : {}),
      }),
      signal: controller.signal,
    });

    const raw = await response.text();
    if (!response.ok) {
      throw mapOpenRouterError(response.status, raw);
    }

    let payload: {choices?: Array<{message?: {content?: string}}>};
    try {
      payload = JSON.parse(raw) as typeof payload;
    } catch {
      throw new ApiError(502, "IA_INVALID_RESPONSE", "La IA devolvió una respuesta inválida.");
    }

    const text = payload.choices?.[0]?.message?.content?.trim() ?? "";
    if (!text) {
      throw new ApiError(502, "IA_INVALID_RESPONSE", "La IA devolvió una respuesta vacía.");
    }
    return text;
  } catch (err) {
    if (err instanceof ApiError) {
      throw err;
    }
    if (err instanceof Error && (err.name === "AbortError" || err.message === "IA_TIMEOUT")) {
      throw new Error("IA_TIMEOUT");
    }
    const message = err instanceof Error ? err.message : String(err);
    if (/429|quota|rate|timeout|aborted|deadline/i.test(message)) {
      throw new Error(message.includes("429") ? message : "IA_TIMEOUT");
    }
    throw new ApiError(503, "IA_UNAVAILABLE", "No se pudo contactar el servicio de IA.");
  } finally {
    clearTimeout(timer);
  }
}

export async function generateJsonAnalisis(
  apiKey: string,
  systemPrompt: string,
  userPrompt: string
): Promise<AnalisisIaResponse> {
  const text = await completeChat(
    apiKey,
    [
      {role: "system", content: systemPrompt},
      {role: "user", content: userPrompt},
    ],
    true
  );
  return parseAnalisis(extractJson(text));
}

export async function generateChatReply(
  apiKey: string,
  systemPrompt: string,
  userPrompt: string
): Promise<string> {
  const text = await completeChat(
    apiKey,
    [
      {role: "system", content: systemPrompt},
      {role: "user", content: userPrompt},
    ],
    false
  );
  return text.slice(0, 2000);
}
