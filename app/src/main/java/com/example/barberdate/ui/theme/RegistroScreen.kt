package com.example.barberdate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.barberdate.ui.theme.BarberGold

@Composable
fun RegistroScreen(
    onRegistroExitoso: (String) -> Unit,
    onVolverInicio: () -> Unit
) {
    // 1. ESTADOS INDEPENDIENTES PARA CADA CAMPO
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    // Estados para controlar los mensajes de error
    var nombreError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    var contrasenaError by remember { mutableStateOf<String?>(null) }

    // 2. FUNCIÓN DE VALIDACIÓN
    fun validarFormulario(): Boolean {
        var esValido = true

        if (nombre.trim().isEmpty()) {
            nombreError = "El nombre es obligatorio"
            esValido = false
        } else if (nombre.trim().length < 3) {
            nombreError = "El nombre debe tener al menos 3 caracteres"
            esValido = false
        } else {
            nombreError = null
        }

        val regexCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")
        if (correo.trim().isEmpty()) {
            correoError = "El correo electrónico es obligatorio"
            esValido = false
        } else if (!regexCorreo.matches(correo.trim())) {
            correoError = "Ingrese un correo con formato válido"
            esValido = false
        } else {
            correoError = null
        }

        if (telefono.trim().isEmpty()) {
            telefonoError = "El teléfono es obligatorio"
            esValido = false
        } else if (telefono.trim().length != 9 || !telefono.all { it.isDigit() }) {
            telefonoError = "El teléfono debe contener exactamente 9 dígitos"
            esValido = false
        } else {
            telefonoError = null
        }

        if (contrasena.trim().isEmpty()) {
            contrasenaError = "La contraseña es obligatoria"
            esValido = false
        } else if (contrasena.trim().length < 6) {
            contrasenaError = "Debe tener al menos 6 caracteres"
            esValido = false
        } else {
            contrasenaError = null
        }

        return esValido
    }

    // FUNCIÓN PARA LIMPIAR EL FORMULARIO
    fun limpiarFormulario() {
        nombre = ""
        correo = ""
        telefono = ""
        contrasena = ""
        nombreError = null
        correoError = null
        telefonoError = null
        contrasenaError = null
    }

    // LAYOUT PRINCIPAL: COLUMN
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Únete a Barber Date para reservar tus citas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- CAMPO 1: NOMBRE ---
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                if (nombreError != null) nombreError = null
            },
            label = { Text("Nombre completo") },
            isError = nombreError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (nombreError != null) {
            Text(
                text = nombreError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- CAMPO 2: CORREO ---
        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                if (correoError != null) correoError = null
            },
            label = { Text("Correo electrónico") },
            isError = correoError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        if (correoError != null) {
            Text(
                text = correoError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- CAMPO 3: TELÉFONO ---
        OutlinedTextField(
            value = telefono,
            onValueChange = {
                if (it.length <= 9) telefono = it
                if (telefonoError != null) telefonoError = null
            },
            label = { Text("Teléfono de contacto (9 dígitos)") },
            isError = telefonoError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        if (telefonoError != null) {
            Text(
                text = telefonoError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- CAMPO 4: CONTRASEÑA ---
        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                if (contrasenaError != null) contrasenaError = null
            },
            label = { Text("Contraseña") },
            isError = contrasenaError != null,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        if (contrasenaError != null) {
            Text(
                text = contrasenaError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // LAYOUT SECUNDARIO: ROW PARA BOTONES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { limpiarFormulario() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpiar")
            }

            Button(
                onClick = {
                    if (validarFormulario()) {
                        onRegistroExitoso(nombre)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BarberGold,
                    contentColor = Color.White
                )
            ) {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onVolverInicio) {
            Text("Volver al inicio")
        }
    }
}