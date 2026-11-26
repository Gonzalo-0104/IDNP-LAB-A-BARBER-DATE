package com.example.barberdate.data.repository

import com.example.barberdate.data.model.Cita
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.firestore.Query


class CitaRepository {

    private val db = FirebaseFirestore.getInstance()

    private val citasCollection = db.collection("citas")

    // Registrar una nueva cita
    suspend fun registrarCita(cita: Cita): Result<String> {
        return try {
            val documento = citasCollection.document()

            val nuevaCita = cita.copy(
                id = documento.id
            )

            documento.set(nuevaCita).await()

            Result.success(documento.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Consultar todas las citas
    suspend fun obtenerCitas(): Result<List<Cita>> {
        return try {
            val snapshot = citasCollection.get().await()

            val citas = snapshot.documents.mapNotNull { documento ->
                documento.toObject(Cita::class.java)
            }

            Result.success(citas)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar el estado de una cita
    suspend fun actualizarEstado(
        citaId: String,
        nuevoEstado: String
    ): Result<Unit> {
        return try {
            citasCollection.document(citaId)
                .update("estado", nuevoEstado)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun observarCitas(): Flow<List<Cita>> = callbackFlow {

        val listener = citasCollection.addSnapshotListener { snapshot, error ->

            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val listaCitas = snapshot?.documents?.mapNotNull { documento ->
                documento.toObject(Cita::class.java)
            } ?: emptyList()

            trySend(listaCitas)
        }

        awaitClose {
            listener.remove()
        }
    }


    // Obtener las citas de un cliente
    suspend fun obtenerCitasCliente(
        clienteId: String
    ): Result<List<Cita>> {
        return try {
            val snapshot = citasCollection
                .whereEqualTo("clienteId", clienteId)
                .get()
                .await()

            val citas = snapshot.documents.mapNotNull {
                it.toObject(Cita::class.java)
            }

            Result.success(citas)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener las citas de un barbero
    suspend fun obtenerCitasBarbero(
        barberoId: String
    ): Result<List<Cita>> {
        return try {
            val snapshot = citasCollection
                .whereEqualTo("barberoId", barberoId)
                .get()
                .await()

            val citas = snapshot.documents.mapNotNull {
                it.toObject(Cita::class.java)
            }

            Result.success(citas)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // Escuchar las citas de un cliente
    fun observarCitasCliente(
        clienteId: String
    ): Flow<List<Cita>> = callbackFlow {

        val listener = citasCollection
            .whereEqualTo("clienteId", clienteId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val citas = snapshot?.documents?.mapNotNull {
                    it.toObject(Cita::class.java)
                } ?: emptyList()

                trySend(citas)
            }

        awaitClose {
            listener.remove()
        }
    }

    // Escuchar las citas de un barbero
    fun observarCitasBarbero(
        barberoId: String
    ): Flow<List<Cita>> = callbackFlow {

        val listener = citasCollection
            .whereEqualTo("barberoId", barberoId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val citas = snapshot?.documents?.mapNotNull {
                    it.toObject(Cita::class.java)
                } ?: emptyList()

                trySend(citas)
            }

        awaitClose {
            listener.remove()
        }
    }
}

