package com.example.barberdate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedButton


@Composable
fun RegistroScreen(
    onRegistroExitoso: (String) -> Unit,
    onVolverInicio: () -> Unit,
    onIniciarComoCliente: () -> Unit,
    onIniciarComoBarbero: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Seleccionar tipo de usuario",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onIniciarComoCliente
        ) {
            Text("Ingresar como Cliente")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onIniciarComoBarbero
        ) {
            Text("Ingresar como Barbero")
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onVolverInicio
        ) {
            Text("Volver")
        }
    }
}
