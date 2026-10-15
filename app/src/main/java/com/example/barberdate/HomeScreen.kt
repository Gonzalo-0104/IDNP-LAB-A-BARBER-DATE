package com.example.barberdate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.barberdate.ui.theme.BarberGold

data class Servicio(
    val nombre: String,
    val precio: String,
    val icono: String
)

@Composable
fun HomeScreen(
    nombreUsuario: String?,
    onNavigateToRegistro: () -> Unit,
    onNavigateToReserva: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val servicios = listOf(
        Servicio("Corte", "Desde $15", "✂"),
        Servicio("Uñas", "Desde $25", "✿"),
        Servicio("Terapia", "Desde $45", "♡"),
        Servicio("Color", "Desde $35", "✎"),
        Servicio("Barba", "Desde $10", "◉"),
        Servicio("Masaje", "Desde $30", "✿")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        // --- Header Dinámico ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (nombreUsuario != null) "Hola, $nombreUsuario 👋" else "Bienvenido 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (nombreUsuario != null) "¿Qué te vas a hacer hoy?" else "Inicia sesión para reservar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Botón de Cerrar Sesión si hay usuario logueado
            if (nombreUsuario != null) {
                TextButton(onClick = onCerrarSesion) {
                    Text("Salir", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Título de sección ---
        Text(
            text = "Servicios",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- Grid adaptable de servicios ---
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(servicios) { servicio ->
                ServicioCard(servicio)
            }
        }

        // --- Acción principal dinámica ---
        if (nombreUsuario == null) {
            // Si no está logueado, pide registro
            OutlinedButton(
                onClick = onNavigateToRegistro,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrarse / Iniciar Sesión", fontWeight = FontWeight.SemiBold)
            }
        } else {
            // Si está logueado, permite reservar y aplica tu color BarberGold
            Button(
                onClick = onNavigateToReserva,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BarberGold,
                    contentColor = Color.White
                )
            ) {
                Text("Reservar Servicios", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ServicioCard(servicio: Servicio) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(text = servicio.icono)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = servicio.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = servicio.precio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}