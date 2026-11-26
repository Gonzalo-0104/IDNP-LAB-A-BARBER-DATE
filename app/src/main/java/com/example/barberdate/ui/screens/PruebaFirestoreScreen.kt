
package com.example.barberdate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.barberdate.data.model.Cita
import com.example.barberdate.ui.viewmodel.CitaViewModel
import com.google.firebase.Timestamp

@Composable
fun PruebaFirestoreScreen(
    citaViewModel: CitaViewModel = viewModel()
) {
    val citas by citaViewModel.citas.collectAsStateWithLifecycle()
    val mensaje by citaViewModel.mensaje.collectAsStateWithLifecycle()
    val cargando by citaViewModel.cargando.collectAsStateWithLifecycle()

    // Rol seleccionado
    var rol by remember {
        mutableStateOf("CLIENTE")
    }

    // Identificador según el rol
    val usuarioId = when (rol) {
        "CLIENTE" -> "cliente_01"
        "BARBERO" -> "barbero_01"
        else -> ""
    }

    // Iniciar escucha en tiempo real
    LaunchedEffect(usuarioId, rol) {
        citaViewModel.observarCitas(
            usuarioId = usuarioId,
            rol = rol
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Prueba de Firestore",
            style = MaterialTheme.typography.headlineSmall
        )

        // Selector de rol
        Text(
            text = "Seleccionar usuario",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { rol = "CLIENTE" },
                enabled = rol != "CLIENTE"
            ) {
                Text("Cliente")
            }

            Button(
                onClick = { rol = "BARBERO" },
                enabled = rol != "BARBERO"
            ) {
                Text("Barbero")
            }
        }

        Text("Rol actual: $rol")
        Text("Usuario: $usuarioId")

        HorizontalDivider()

        // Registrar cita
        Button(
            onClick = {
                citaViewModel.registrarCitaConWorkManager(
                    Cita(
                        clienteId = "cliente_01",
                        barberoId = "barbero_01",
                        servicio = "Corte de cabello",
                        fecha = Timestamp.now(),
                        estado = "PENDIENTE",
                        creadoEn = Timestamp.now()
                    )
                )
            },
            enabled = !cargando
        ) {
            Text("Registrar cita de prueba")
        }

        if (cargando) {
            CircularProgressIndicator()
        }

        mensaje?.let {
            Text(text = it)
        }

        HorizontalDivider()

        // Lista filtrada en tiempo real
        Text(
            text = "Citas registradas: ${citas.size}",
            style = MaterialTheme.typography.titleMedium
        )

        citas.forEach { cita ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text("Servicio: ${cita.servicio}")
                    Text("Cliente: ${cita.clienteId}")
                    Text("Barbero: ${cita.barberoId}")
                    Text("Estado: ${cita.estado}")
                    Text("ID: ${cita.id}")
                }
            }
        }
    }
}