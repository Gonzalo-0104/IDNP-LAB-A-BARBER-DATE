package com.example.barberdate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.barberdate.ui.theme.BarberDateTheme
import androidx.compose.ui.text.rememberTextMeasurer
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarberDateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

// 1. Primera pantalla del flujo de laboratorio: Formulario de Reserva con Estado y Validación
@Composable
fun PrimeraPantalla(onNavigate: (String) -> Unit) {
    var nombreCliente by remember { mutableStateOf("") }
    var mostrarError by remember { mutableStateOf(false) }
    val nameApp = "Barber Date"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 500.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TituloReserva(nameApp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombreCliente,
                onValueChange = {
                    nombreCliente = it
                    if (mostrarError) mostrarError = false
                },
                label = { Text("Nombre del cliente") },
                isError = mostrarError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (mostrarError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "El nombre no puede estar vacío",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombreCliente.isNotBlank()) {
                        onNavigate(nombreCliente)
                    } else {
                        mostrarError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Reserva")
            }
        }
    }
}

// 1.1 Composable Title
@Composable
fun TituloReserva(nameApp: String) {
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.headlineSmall

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val textoCompleto = "Reservar en $nameApp"

        val anchoTexto = textMeasurer.measure(
            text = textoCompleto,
            style = style
        ).size.width

        if (anchoTexto <= constraints.maxWidth) {

            // Todo cabe en una línea
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reservar en",
                    style = style
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = nameApp,
                    style = style,
                    softWrap = false
                )
            }

        } else {

            // No cabe → dos líneas
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Reservar en",
                    style = style
                )

                Text(
                    text = nameApp,
                    style = style,
                    softWrap = false
                )
            }
        }
    }
}

// 2. Segunda pantalla del flujo de laboratorio: Confirmación y Retorno
@Composable
fun SegundaPantalla(nombreRecibido: String, onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Cita confirmada para:", style = MaterialTheme.typography.titleMedium)
        Text(
            text = nombreRecibido,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onNavigateBack) {
            Text("Volver al inicio")
        }
    }
}

// 3. Sistema de Navegación completo que vincula HomeScreen, PrimeraPantalla y SegundaPantalla
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        // Ruta 1: Catálogo de servicios (HomeScreen)
        composable("home") {
            HomeScreen(
                onNavigateToReserva = {
                    navController.navigate("reserva")
                }
            )
        }

        // Ruta 2: Captura de nombre con validación (PrimeraPantalla)
        composable("reserva") {
            PrimeraPantalla(onNavigate = { nombre ->
                navController.navigate("confirmacion/$nombre")
            })
        }

        // Ruta 3: Muestra de resultado recibido (SegundaPantalla)
        composable(
            route = "confirmacion/{nombre}",
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            SegundaPantalla(
                nombreRecibido = nombre,
                onNavigateBack = {
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
    }
}