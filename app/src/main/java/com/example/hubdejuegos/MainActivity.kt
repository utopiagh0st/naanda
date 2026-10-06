package com.example.hubdejuegos

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hubdejuegos.model.*
import com.example.hubdejuegos.ui.theme.HubDeJuegosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HubDeJuegosTheme {
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

    // Lista y estado para la creación de juegos
    val listaJuegos = remember { mutableStateListOf<Juego>() }
    var nombreJuego by remember { mutableStateOf("") }
    var descripcionJuego by remember { mutableStateOf("") }
    var categoriaJuego by remember { mutableStateOf("Acción") }
    var versionJuego by remember { mutableStateOf("1.0.0") }
    var imagenPortada by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }

    val categoriasDisponibles = listOf("Acción", "Aventura", "Puzzle", "Estrategia", "RPG", "Deportes", "Simulación")

    // Launcher para abrir la galería y seleccionar una imagen
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imagenPortada = uri.toString()
        }
    }

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
                                Text("➕ Crear Nuevo Juego", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                OutlinedTextField(
                                    value = nombreJuego,
                                    onValueChange = { nombreJuego = it },
                                    label = { Text("Nombre del juego") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = descripcionJuego,
                                    onValueChange = { descripcionJuego = it },
                                    label = { Text("Descripción") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Categoría:", fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    categoriasDisponibles.forEach { cat ->
                                        FilterChip(
                                            selected = categoriaJuego == cat,
                                            onClick = { categoriaJuego = cat },
                                            label = { Text(cat) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = versionJuego,
                                    onValueChange = { versionJuego = it },
                                    label = { Text("Versión (ej. 1.0.0)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = imagenPortada,
                                    onValueChange = { imagenPortada = it },
                                    label = { Text("Imagen de portada (URI o ruta)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        galleryLauncher.launch("image/*")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("🖼️ Seleccionar Imagen de la Galería")
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        if (nombreJuego.isNotBlank()) {
                                            val nuevoJuego = Juego(
                                                id = listaJuegos.size + 1,
                                                nombre = nombreJuego,
                                                descripcion = descripcionJuego,
                                                categoria = categoriaJuego,
                                                version = versionJuego,
                                                imagenPortada = imagenPortada
                                            )
                                            listaJuegos.add(nuevoJuego)
                                            mensajeExito = "¡Juego '${nuevoJuego.nombre}' creado y añadido a la lista!"
                                            // Reset form
                                            nombreJuego = ""
                                            descripcionJuego = ""
                                            categoriaJuego = "Acción"
                                            versionJuego = "1.0.0"
                                            imagenPortada = ""
                                        } else {
                                            mensajeExito = "Por favor, ingresa al menos el nombre del juego."
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Guardar y Añadir Juego")
                                }

                                if (mensajeExito.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = mensajeExito,
                                        color = if (mensajeExito.startsWith("¡")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        if (listaJuegos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("📋 Juegos Creados (${listaJuegos.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    listaJuegos.forEach { juego ->
                                        Text("• ${juego.nombre} (v${juego.version}) - Cat: ${juego.categoria} [Portada: ${juego.imagenPortada.ifBlank { "Sin imagen" }}]")
                                    }
                                }
                            }
                        }
                    }
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
    HubDeJuegosTheme {
        MainScreen()
    }
}
