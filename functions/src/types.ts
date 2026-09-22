export type NivelIa = "alto" | "precaucion" | "normal";

export interface AnalizarConsumoPayload {
  habitantes: number;
  consumo_hoy_litros: number;
  promedio_7_dias_litros: number;
  variacion_porcentaje: number;
  meta_mensual_litros: number;
  consumo_acumulado_litros: number;
  dias_transcurridos: number;
  patron_detectado: string;
}

export interface AnalisisIaResponse {
  nivel: NivelIa;
  explicacion: string;
  recomendaciones: string[];
  advertencia: string;
}

export interface ChatRequestPayload {
  viviendaId: number | string;
  mensaje: string;
}

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    public readonly code: string,
    message: string
  ) {
    super(message);
    this.name = "ApiError";
  }
}

export const NIVELES: readonly NivelIa[] = ["alto", "precaucion", "normal"];
