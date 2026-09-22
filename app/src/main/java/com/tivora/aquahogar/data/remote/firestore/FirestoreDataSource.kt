package com.tivora.aquahogar.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.model.RegistroConsumo
import com.tivora.aquahogar.domain.model.Vivienda
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun uidOrNull(): String? = auth.currentUser?.uid

    private fun viviendaDoc(uid: String) = db.collection("viviendas").document(uid)

    suspend fun upsertVivienda(vivienda: Vivienda) {
        val uid = uidOrNull() ?: return
        viviendaDoc(uid).set(
            mapOf(
                "ownerId" to uid,
                "nombre" to vivienda.nombre.take(80),
                "numeroHabitantes" to vivienda.numeroHabitantes,
                "metaMensualLitros" to vivienda.metaMensualLitros,
                "localId" to vivienda.id,
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()
    }

    suspend fun upsertRegistro(registro: RegistroConsumo) {
        val uid = uidOrNull() ?: return
        viviendaDoc(uid).collection("registros").document("reg_${registro.id}").set(
            mapOf(
                "ownerId" to uid,
                "localId" to registro.id,
                "fecha" to registro.fecha,
                "lecturaMedidorM3" to registro.lecturaMedidorM3
            )
        ).await()
    }

    suspend fun upsertAlerta(alerta: AlertaConsumo, fuente: String = "local") {
        val uid = uidOrNull() ?: return
        val docId = if (alerta.id > 0) "alerta_${alerta.id}" else "alerta_${alerta.fecha}"
        viviendaDoc(uid).collection("alertas").document(docId).set(
            mapOf(
                "ownerId" to uid,
                "localId" to alerta.id,
                "fecha" to alerta.fecha,
                "nivel" to alerta.nivel.name.lowercase(),
                "porcentajeVariacion" to alerta.porcentajeVariacion,
                "descripcion" to alerta.descripcion.take(2000),
                "fuente" to fuente
            ),
            SetOptions.merge()
        ).await()
    }
}
