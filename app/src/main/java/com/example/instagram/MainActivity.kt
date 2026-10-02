package com.example.instagram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagram.model.*
import com.example.instagram.ui.theme.InstagramTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstagramTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedOption by remember { mutableStateOf("ESTADISTICAS") }

    val usuario = remember {
        Usuario(
            id = 1,
            nombre = "RodoXD0606",
            email = "rodo@gmail.com",
            contrasena = "123456",
            rol = Rol.JUGADOR
        )
    }

    var puntuacion by remember { mutableIntStateOf(450) }
    var nivel by remember { mutableIntStateOf(5) }
    var tiempoJuego by remember { mutableLongStateOf(120L) }
    var isPremium by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "HubJuegos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botones de acción principal (Jugar, Crear, Estadísticas)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { selectedOption = "JUGAR" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedOption == "JUGAR") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedOption == "JUGAR") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("🎮 Jugar")
                }

                Button(
                    onClick = { selectedOption = "CREAR" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedOption == "CREAR") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedOption == "CREAR") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("➕ Crear")
                }

                Button(
                    onClick = { selectedOption = "ESTADISTICAS" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedOption == "ESTADISTICAS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedOption == "ESTADISTICAS") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("📊 Estadísticas")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedOption) {
                "JUGAR" -> {
                    // Pantalla en blanco
                }
                "CREAR" -> {
                    // Pantalla en blanco
                }
                "ESTADISTICAS" -> {
                    val jugadorActual = if (isPremium) {
                        JugadorPremium(id = 101, usuario = usuario, puntuacion = puntuacion, nivel = nivel)
                    } else {
                        JugadorNormal(id = 101, usuario = usuario, puntuacion = puntuacion, nivel = nivel, tiempoJuegoTotal = tiempoJuego)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("👤 Datos del Usuario", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Text("ID: ${usuario.id}")
                                Text("Nombre: ${usuario.nombre}")
                                Text("Email: ${usuario.email}")
                                Text("Rol: ${usuario.rol}")
                                Text("Fecha de Registro: ${usuario.fechaRegistro}")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("📊 Estadísticas del Jugador", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Text("Nivel: ${jugadorActual.nivel}")
                                Text("Puntuación: ${jugadorActual.puntuacion} pts")

                                Spacer(modifier = Modifier.height(4.dp))
                                if (jugadorActual is JugadorNormal) {
                                    Text("Tipo: Jugador Normal", fontWeight = FontWeight.Medium)
                                    Text(jugadorActual.obtenerEstadisticas())
                                } else if (jugadorActual is JugadorPremium) {
                                    Text("Tipo: Jugador Premium ⭐", fontWeight = FontWeight.Medium)
                                    Text("Beneficios: ${jugadorActual.obtenerBeneficios()}")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text("Acciones de Prueba:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(onClick = {
                                puntuacion += 100
                                if (puntuacion >= nivel * 100) nivel++
                            }) {
                                Text("+100 Pts")
                            }

                            OutlinedButton(onClick = {
                                tiempoJuego += 30
                            }) {
                                Text("+30m Tiempo")
                            }

                            FilterChip(
                                selected = isPremium,
                                onClick = { isPremium = !isPremium },
                                label = { Text(if (isPremium) "Premium" else "Normal") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    InstagramTheme {
        MainScreen()
    }
}
