package com.example.barberdate

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class CitaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CitaUiState())
    val uiState: StateFlow<CitaUiState> = _uiState.asStateFlow()

    fun avanzarUnDia() {
        _uiState.update { estado ->
            estado.copy(
                fechaSimulada = estado.fechaSimulada.plusDays(1),
                diasSimulados = estado.diasSimulados + 1
            )
        }
    }

    fun reiniciarSimulacion() {
        _uiState.value = CitaUiState()
    }

    fun cambiarFrecuencia(frecuencia: FrecuenciaCita) {
        _uiState.update { estado ->
            estado.copy(frecuencia = frecuencia)
        }
    }
}