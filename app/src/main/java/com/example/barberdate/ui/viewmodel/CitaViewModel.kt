package com.example.barberdate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barberdate.data.model.Cita
import com.example.barberdate.data.repository.CitaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.barberdate.data.worker.CitaWorker

class CitaViewModel : ViewModel() {

    private val repository = CitaRepository()

    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    val citas: StateFlow<List<Cita>> = _citas.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private var escuchaCitasJob: Job? = null

    // Escuchar citas según el rol del usuario
    fun observarCitas(usuarioId: String, rol: String) {

        escuchaCitasJob?.cancel()
        _citas.value = emptyList()

        val flujo = when (rol) {
            "CLIENTE" -> repository.observarCitasCliente(usuarioId)
            "BARBERO" -> repository.observarCitasBarbero(usuarioId)
            else -> {
                _mensaje.value = "Rol de usuario no válido"
                return
            }
        }

        escuchaCitasJob = viewModelScope.launch {
            flujo
                .catch { error ->
                    _mensaje.value =
                        "Error al escuchar citas: ${error.message}"
                }
                .collect { lista ->
                    _citas.value = lista
                }
        }
    }

    // Registrar una cita
    fun registrarCita(cita: Cita) {
        viewModelScope.launch {
            _cargando.value = true

            val resultado = repository.registrarCita(cita)

            resultado.onSuccess {
                _mensaje.value = "Cita registrada correctamente"
            }.onFailure { error ->
                _mensaje.value =
                    "Error al registrar: ${error.message}"
            }

            _cargando.value = false
        }
    }

    // Actualizar el estado de una cita
    fun actualizarEstado(citaId: String, estado: String) {
        viewModelScope.launch {
            val resultado =
                repository.actualizarEstado(citaId, estado)

            resultado.onSuccess {
                _mensaje.value = "Estado actualizado correctamente"
            }.onFailure { error ->
                _mensaje.value =
                    "Error al actualizar: ${error.message}"
            }
        }
    }

    // Consulta general, conservada únicamente para pruebas
    fun obtenerCitas() {
        viewModelScope.launch {
            _cargando.value = true

            val resultado = repository.obtenerCitas()

            resultado.onSuccess { lista ->
                _citas.value = lista
            }.onFailure { error ->
                _mensaje.value =
                    "Error al consultar: ${error.message}"
            }

            _cargando.value = false
        }
    }

    fun registrarCitaConWorkManager(cita: Cita) {

        val restricciones = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val solicitud = OneTimeWorkRequestBuilder<CitaWorker>()
            .setConstraints(restricciones)
            .setInputData(
                workDataOf(
                    "clienteId" to cita.clienteId,
                    "barberoId" to cita.barberoId,
                    "servicio" to cita.servicio
                )
            )
            .build()

        WorkManager.getInstance()
            .enqueue(solicitud)

        _mensaje.value =
            "Cita encolada. Se enviará cuando haya conexión."
    }

}
