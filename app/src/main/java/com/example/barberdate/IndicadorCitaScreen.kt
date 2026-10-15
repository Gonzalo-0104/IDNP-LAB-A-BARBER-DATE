package com.example.barberdate

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import java.time.format.DateTimeFormatter

@Composable
fun IndicadorCitaScreen(
    viewModel: CitaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    val progresoAnimado by animateFloatAsState(
        targetValue = estado.progreso,
        animationSpec = tween(durationMillis = 700),
        label = "ProgresoCita"
    )

    val fechaFormato = estado.fechaSimulada.format(
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    )

    val colorProgreso = MaterialTheme.colorScheme.primary
    val colorFondo = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tu próxima cita",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(8.dp))

        Text("Hoy (simulado): $fechaFormato")

        Text(
            text = "Frecuencia: ${estado.frecuencia.name}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        // Indicador circular animado.
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val grosor = 14.dp.toPx()
                val margen = grosor / 2

                drawArc(
                    color = colorFondo,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        margen, margen
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        size.width - grosor,
                        size.height - grosor
                    ),
                    style = Stroke(width = grosor)
                )

                drawArc(
                    color = colorProgreso,
                    startAngle = -90f,
                    sweepAngle = 360f * progresoAnimado,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        margen, margen
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        size.width - grosor,
                        size.height - grosor
                    ),
                    style = Stroke(
                        width = grosor,
                        cap = StrokeCap.Round
                    )
                )
            }

            Text(
                text = "${estado.diasRestantes}",
                style = MaterialTheme.typography.displayMedium
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = when (estado.diasRestantes) {
                0 -> "¡Hoy tienes cita!"
                1 -> "¡Tu cita es mañana!"
                else -> "Faltan ${estado.diasRestantes} días"
            },
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.avanzarUnDia() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Avanzar un día")
        }

        OutlinedButton(
            onClick = { viewModel.reiniciarSimulacion() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reiniciar prueba")
        }

        Spacer(Modifier.height(16.dp))

        Text("Probar otra frecuencia:")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FrecuenciaCita.entries.forEach { frecuencia ->
                TextButton(
                    onClick = {
                        viewModel.cambiarFrecuencia(frecuencia)
                    }
                ) {
                    Text(
                        when (frecuencia) {
                            FrecuenciaCita.SEMANAL -> "Semanal"
                            FrecuenciaCita.QUINCENAL -> "Quincenal"
                            FrecuenciaCita.MENSUAL -> "Mensual"
                        }
                    )
                }
            }
        }
    }
}