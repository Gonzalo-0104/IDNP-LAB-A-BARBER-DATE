package com.example.barberdate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.barberdate.data.AppDatabase
import com.example.barberdate.data.EspecialistaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EspecialistasRoomScreen(onVolver: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = remember { db.especialistaDao() }
    val scope = rememberCoroutineScope()

    // Observa la base de datos en tiempo real (Flow)
    val listaEspecialistas by dao.obtenerTodos().collectAsState(initial = emptyList())

    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") }
    var mostrarError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Especialistas (Room)") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- FORMULARIO DE REGISTRO ---
            Text(
                text = "Registrar nuevo especialista",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    mostrarError = false
                },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = especialidad,
                onValueChange = {
                    especialidad = it
                    mostrarError = false
                },
                label = { Text("Especialidad (ej. Fade, Barba)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = experiencia,
                onValueChange = {
                    experiencia = it
                    mostrarError = false
                },
                label = { Text("Experiencia (ej. 5 años)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (mostrarError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Completa todos los campos para guardar",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (nombre.isNotBlank() && especialidad.isNotBlank() && experiencia.isNotBlank()) {
                        val nuevo = EspecialistaEntity(
                            nombre = nombre.trim(),
                            especialidad = especialidad.trim(),
                            experiencia = experiencia.trim()
                        )
                        // Inserción en hilo secundario (IO)
                        scope.launch(Dispatchers.IO) {
                            dao.insertar(nuevo)
                        }
                        // Limpiar campos
                        nombre = ""
                        especialidad = ""
                        experiencia = ""
                    } else {
                        mostrarError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Especialista")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // --- LISTADO REACTIVO CON LAZYCOLUMN ---
            Text(
                text = "Registrados (${listaEspecialistas.size})",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (listaEspecialistas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay especialistas registrados aún.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = listaEspecialistas,
                        key = { it.id }
                    ) { especialista ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = especialista.nombre,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Especialidad: ${especialista.especialidad}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Experiencia: ${especialista.experiencia}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Operación adicional: Eliminar
                                IconButton(
                                    onClick = {
                                        scope.launch(Dispatchers.IO) {
                                            dao.eliminar(especialista)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar especialista",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}