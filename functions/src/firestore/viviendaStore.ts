import * as admin from "firebase-admin";

export function viviendaRef(uid: string): admin.firestore.DocumentReference {
  return admin.firestore().collection("viviendas").doc(uid);
}

export async function loadViviendaContext(uid: string): Promise<Record<string, unknown>> {
  const viviendaSnap = await viviendaRef(uid).get();
  const [registrosSnap, alertasSnap] = await Promise.all([
    viviendaRef(uid).collection("registros").orderBy("fecha", "desc").limit(14).get(),
    viviendaRef(uid).collection("alertas").orderBy("fecha", "desc").limit(5).get(),
  ]);

  return {
    vivienda: viviendaSnap.exists ? viviendaSnap.data() : null,
    registros: registrosSnap.docs.map((doc) => doc.data()),
    alertas: alertasSnap.docs.map((doc) => doc.data()),
  };
}

export async function persistAnalisisIa(
  uid: string,
  variacion: number,
  analisis: {
    nivel: string;
    explicacion: string;
    recomendaciones: string[];
    advertencia: string;
  }
): Promise<void> {
  await viviendaRef(uid).collection("alertas").doc("ultimo_analisis_ia").set({
    ownerId: uid,
    localId: 0,
    fecha: Date.now(),
    nivel: analisis.nivel,
    porcentajeVariacion: variacion,
    descripcion: analisis.explicacion,
    explicacion: analisis.explicacion,
    recomendaciones: analisis.recomendaciones,
    advertencia: analisis.advertencia,
    fuente: "ia",
  });
}
