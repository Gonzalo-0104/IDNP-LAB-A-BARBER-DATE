package com.example.barberdate

import java.time.LocalDate

enum class FrecuenciaCita(val intervaloDias: Int) {
    SEMANAL(7),
    QUINCENAL(15),
    MENSUAL(30) // Aproximación para la prueba
}

data class CitaUiState(
    val fechaSimulada: LocalDate = LocalDate.now(),
    val frecuencia: FrecuenciaCita = FrecuenciaCita.SEMANAL,
    val diasSimulados: Int = 0
) {
    val diasRestantes: Int
        get() {
            val resto = diasSimulados % frecuencia.intervaloDias

            return if (diasSimulados > 0 && resto == 0) {
                0
            } else {
                frecuencia.intervaloDias - resto
            }
        }

    val progreso: Float
        get() {
            val resto = diasSimulados % frecuencia.intervaloDias

            return if (diasSimulados > 0 && resto == 0) {
                1f
            } else {
                resto.toFloat() / frecuencia.intervaloDias
            }
        }
}
