import {NextFunction, Request, Response} from "express";
import * as admin from "firebase-admin";

export async function requireAuth(
  req: Request,
  res: Response,
  next: NextFunction
): Promise<void> {
  const header = req.headers.authorization;
  if (!header?.startsWith("Bearer ")) {
    res.status(401).json({
      code: "UNAUTHENTICATED",
      message: "Inicia sesión para continuar.",
    });
    return;
  }

  try {
    const decoded = await admin.auth().verifyIdToken(header.slice(7));
    req.uid = decoded.uid;
    next();
  } catch (err) {
    console.warn("Invalid ID token", err);
    res.status(401).json({
      code: "UNAUTHENTICATED",
      message: "La sesión expiró. Vuelve a iniciar sesión.",
    });
  }
}
