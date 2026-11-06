package com.example.barberdate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de tema que usa AndroidViewModel para acceder al Context de la app
 * y así crear el repositorio con acceso al DataStore.
 *
 * Expone [temaOscuro] como StateFlow para que la UI observe el valor
 * y se recomponga automáticamente cuando cambia.
 */
class TemaViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = TemaRepository(application.applicationContext)

    /**
     * Estado del tema observable por la UI.
     * - Valor inicial: `false` (tema claro) mientras DataStore no haya emitido nada.
     * - SharingStarted.WhileSubscribed: detiene la suscripción cuando no hay
     *   colectores activos para ahorrar recursos.
     */
    val temaOscuro: StateFlow<Boolean> = repositorio.temaOscuroFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    /**
     * Invierte el tema actual y lo persiste en DataStore.
     * Se ejecuta en la coroutine del ViewModel, nunca en el hilo principal.
     */
    fun toggleTema() {
        viewModelScope.launch {
            repositorio.guardarTema(!temaOscuro.value)
        }
    }
}
