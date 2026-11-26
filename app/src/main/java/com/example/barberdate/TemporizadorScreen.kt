package com.example.barberdate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemporizadorScreen(onVolver: () -> Unit) {
    val context = LocalContext.current
    val segundos by TemporizadorBarberService.segundosTranscurridos.collectAsStateWithLifecycle()
    val estaActivo by TemporizadorBarberService.estaActivo.collectAsStateWithLifecycle()

    var mensajePermiso by remember { mutableStateOf<String?>(null) }

    val iniciarServicio = {
        val intent = Intent(context, TemporizadorBarberService::class.java).apply {
            action = TemporizadorBarberService.ACTION_START
        }
        ContextCompat.startForegroundService(context, intent)
    }

    val detenerServicio = {
        val intent = Intent(context, TemporizadorBarberService::class.java).apply {
            action = TemporizadorBarberService.ACTION_STOP
        }
        context.startService(intent)
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            mensajePermiso = null
            iniciarServicio()
        } else {
            mensajePermiso = "Se requiere permiso de notificaciones para mostrar el temporizador persistente."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Temporizador de Servicio") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (estaActivo) "Servicio en curso (Primer plano)" else "Temporizador detenido",
                style = MaterialTheme.typography.titleMedium,
                color = if (estaActivo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = TemporizadorBarberService.formatearTiempo(segundos),
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val tienePermiso = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (tienePermiso) {
                            iniciarServicio()
                        } else {
                            permisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        iniciarServicio()
                    }
                },
                enabled = !estaActivo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Temporizador")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { detenerServicio() },
                enabled = estaActivo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Detener Servicio")
            }

            if (mensajePermiso != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = mensajePermiso!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}