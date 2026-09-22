import cors from "cors";
import express from "express";
import {initializeApp} from "firebase-admin/app";
import {defineSecret} from "firebase-functions/params";
import {onRequest} from "firebase-functions/v2/https";
import {setGlobalOptions} from "firebase-functions/v2/options";
import {analizarConsumo} from "./http/analizarConsumo";
import {requireAuth} from "./http/authMiddleware";
import {chatAsistente} from "./http/chatAsistente";
import {getAlertas, getLecturas, postLectura} from "./http/viviendas";

initializeApp();

const openRouterApiKey = defineSecret("OPENROUTER_API_KEY");

setGlobalOptions({
  region: "us-central1",
  maxInstances: 10,
});

function resolveOpenRouterKey(): string {
  const fromEnv = process.env.OPENROUTER_API_KEY;
  if (fromEnv) {
    return fromEnv;
  }
  return openRouterApiKey.value();
}

const app = express();
app.use(cors({origin: true}));
app.use(express.json({limit: "32kb"}));

app.use(requireAuth);

app.post("/analizarConsumoIA", (req, res) => {
  void analizarConsumo(req, res, resolveOpenRouterKey());
});
app.post("/asistente/chat", (req, res) => {
  void chatAsistente(req, res, resolveOpenRouterKey());
});
app.get("/viviendas/:id/lecturas", (req, res) => {
  void getLecturas(req, res);
});
app.post("/viviendas/:id/lecturas", (req, res) => {
  void postLectura(req, res);
});
app.get("/viviendas/:id/alertas", (req, res) => {
  void getAlertas(req, res);
});

app.use((_req, res) => {
  res.status(404).json({code: "NOT_FOUND", message: "Ruta no encontrada."});
});

export const api = onRequest(
  {
    secrets: [openRouterApiKey],
    timeoutSeconds: 60,
    memory: "256MiB",
    cors: true,
  },
  app
);
