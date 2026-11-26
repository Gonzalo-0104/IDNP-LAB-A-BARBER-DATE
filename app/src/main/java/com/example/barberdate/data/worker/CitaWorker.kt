package com.example.barberdate.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.barberdate.data.model.Cita
import com.example.barberdate.data.repository.CitaRepository
import kotlinx.coroutines.CancellationException

class CitaWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val clienteId = inputData.getString("clienteId")
            ?: return Result.failure()

        val barberoId = inputData.getString("barberoId")
            ?: return Result.failure()

        val servicio = inputData.getString("servicio")
            ?: return Result.failure()

        val cita = Cita(
            clienteId = clienteId,
            barberoId = barberoId,
            servicio = servicio,
            estado = "PENDIENTE",
            creadoEn = com.google.firebase.Timestamp.now()
        )

        return try {
            val repository = CitaRepository()
            val resultado = repository.registrarCita(cita)

            resultado.fold(
                onSuccess = {
                    Result.success()
                },
                onFailure = { error ->
                    if (error is CancellationException) {
                        throw error
                    }
                    Result.retry()
                }
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.retry()
        }
    }
}