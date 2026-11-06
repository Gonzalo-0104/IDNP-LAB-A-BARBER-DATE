package com.example.barberdate

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión que crea (o reutiliza) el archivo de DataStore llamado "ajustes_usuario"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "ajustes_usuario"
)

/**
 * Repositorio responsable de leer y escribir la preferencia de tema oscuro.
 *
 * - Lee: expone un Flow<Boolean> para que la UI observe cambios en tiempo real.
 * - Escribe: función suspend que actualiza el valor de forma asíncrona, sin
 *   bloquear el hilo principal.
 */
class TemaRepository(private val context: Context) {

    companion object {
        // Clave tipada que identifica la preferencia dentro del DataStore
        val CLAVE_TEMA_OSCURO = booleanPreferencesKey("tema_oscuro")
    }

    /**
     * Flujo que emite el valor actual de la preferencia cada vez que cambia.
     * Si aún no hay valor guardado, emite `false` (tema claro por defecto).
     */
    val temaOscuroFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[CLAVE_TEMA_OSCURO] ?: false
    }

    /**
     * Guarda el nuevo valor de la preferencia de tema.
     * Debe llamarse desde una coroutine (ViewModel o lifecycleScope).
     */
    suspend fun guardarTema(oscuro: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CLAVE_TEMA_OSCURO] = oscuro
        }
    }
}
