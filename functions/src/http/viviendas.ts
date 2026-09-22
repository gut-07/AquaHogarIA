import {Request, Response} from "express";
import {viviendaRef} from "../firestore/viviendaStore";
import {ApiError} from "../types";
import {sendError} from "./errors";

function asNumber(value: unknown, field: string): number {
  const n = typeof value === "number" ? value : Number(value);
  if (!Number.isFinite(n)) {
    throw new ApiError(400, "INVALID_ARGUMENT", `El campo ${field} debe ser numérico.`);
  }
  return n;
}

export async function getLecturas(req: Request, res: Response): Promise<void> {
  try {
    const snap = await viviendaRef(req.uid)
      .collection("registros")
      .orderBy("fecha", "asc")
      .get();
    const viviendaId = Number(req.params.id) || 0;
    const lecturas = snap.docs.map((doc) => {
      const data = doc.data();
      return {
        id: Number(data.localId ?? 0),
        viviendaId,
        fecha: Number(data.fecha ?? 0),
        lecturaMedidorM3: Number(data.lecturaMedidorM3 ?? 0),
      };
    });
    res.status(200).json(lecturas);
  } catch (err) {
    sendError(res, err);
  }
}

export async function postLectura(req: Request, res: Response): Promise<void> {
  try {
    const viviendaId = Number(req.params.id) || 0;
    const fecha = asNumber(req.body?.fecha, "fecha");
    const lecturaMedidorM3 = asNumber(req.body?.lecturaMedidorM3, "lecturaMedidorM3");
    const localId = req.body?.id != null ? asNumber(req.body.id, "id") : Date.now();
    const docId = `reg_${localId}`;

    const payload = {
      ownerId: req.uid,
      localId,
      fecha,
      lecturaMedidorM3,
    };
    await viviendaRef(req.uid).collection("registros").doc(docId).set(payload);
    res.status(200).json({
      id: localId,
      viviendaId,
      fecha,
      lecturaMedidorM3,
    });
  } catch (err) {
    sendError(res, err);
  }
}

export async function getAlertas(req: Request, res: Response): Promise<void> {
  try {
    const viviendaId = Number(req.params.id) || 0;
    const snap = await viviendaRef(req.uid)
      .collection("alertas")
      .orderBy("fecha", "desc")
      .get();
    const alertas = snap.docs.map((doc) => {
      const data = doc.data();
      return {
        id: Number(data.localId ?? 0),
        viviendaId,
        fecha: Number(data.fecha ?? 0),
        nivel: String(data.nivel ?? "normal"),
        porcentajeVariacion: Number(data.porcentajeVariacion ?? 0),
        descripcion: String(data.descripcion ?? ""),
      };
    });
    res.status(200).json(alertas);
  } catch (err) {
    sendError(res, err);
  }
}
