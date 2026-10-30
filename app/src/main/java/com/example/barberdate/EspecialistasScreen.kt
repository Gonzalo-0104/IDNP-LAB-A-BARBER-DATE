package com.example.barberdate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 1. Data class con más de 4 atributos (id, nombre, especialidad, experiencia, icono)
data class Especialista(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val experiencia: String,
    val icono: String
)

// 2. Colección de al menos 20 registros
fun generarEspecialistas(): List<Especialista> {
    val nombres = listOf("Carlos", "Ana", "Miguel", "Lucía", "Jorge", "Sofía", "Pedro", "María", "David", "Elena")
    val especialidades = listOf("Barbero Clásico", "Colorista", "Especialista en Uñas", "Masajista", "Estilista")
    val iconos = listOf("✂️", "🎨", "💅", "💆", "💇")

    return List(20) { index ->
        val tipo = index % 5
        Especialista(
            id = index + 1, // Clave estable única
            nombre = "${nombres[index % 10]} ${index + 1}",
            especialidad = especialidades[tipo],
            experiencia = "${(2..10).random()} años de exp.",
            icono = iconos[tipo]
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EspecialistasScreen(onVolver: () -> Unit) {
    val especialistas = remember { generarEspecialistas() }
    var especialistaSeleccionado by remember { mutableStateOf<Especialista?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuestros Especialistas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        // 4. LazyColumn para mostrar la colección eficientemente
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // 5. Separación visual
        ) {
            // Se usa "key = { it.id }" para identificar de forma estable los elementos
            items(especialistas, key = { it.id }) { especialista ->
                // 3. Composable reutilizable
                EspecialistaCard(
                    especialista = especialista,
                    onClick = { especialistaSeleccionado = especialista }
                )
            }
        }
    }

    // 6. Al seleccionar un elemento, mostrar un detalle breve
    especialistaSeleccionado?.let { especialista ->
        AlertDialog(
            onDismissRequest = { especialistaSeleccionado = null },
            title = { Text("Detalle del Especialista") },
            text = {
                Column {
                    Text("Nombre: ${especialista.nombre}", fontWeight = FontWeight.Bold)
                    Text("Especialidad: ${especialista.especialidad}")
                    Text("Experiencia: ${especialista.experiencia}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("¡Reserva tu cita con ${especialista.nombre} pronto!", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { especialistaSeleccionado = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

// Componente visual reutilizable para cada ítem de la lista
@Composable
fun EspecialistaCard(especialista: Especialista, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Acción de selección
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = especialista.icono, style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = especialista.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = especialista.especialidad, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                Text(text = especialista.experiencia, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}