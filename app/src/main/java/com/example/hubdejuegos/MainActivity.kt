package com.example.hubdejuegos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hubdejuegos.model.*
import com.example.hubdejuegos.ui.theme.HubDeJuegosTheme
import java.time.format.DateTimeFormatter

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
    var selectedOption by remember { mutableStateOf("JUGAR") }

    val usuario = remember {
        Usuario(
            id = 1,
            nombre = "RodoXD0606",
            email = "rodo@gmail.com",
            contrasena = "123456",
            rol = Rol.JUGADOR
        )
    }

    // Lista de juegos disponibles (creados por el usuario y por otros creadores)
    val juegosList = remember {
        listOf(
            Juego(
                id = 1,
                nombre = "Pixel Runner 2D",
                descripcion = "Juego de plataformas dinámico con esquivas y saltos de precisión.",
                categoria = "Plataforma",
                version = "1.2.0",
                creadorNombre = usuario.nombre,
                creadorId = usuario.id,
                estado = EstadoJuego.APROBADO
            ),
            Juego(
                id = 2,
                nombre = "Space Shooter 3D",
                descripcion = "Defiende la galaxia de invasores alienígenas con naves personalizables.",
                categoria = "Acción",
                version = "1.0.0",
                creadorNombre = usuario.nombre,
                creadorId = usuario.id,
                estado = EstadoJuego.APROBADO
            ),
            Juego(
                id = 3,
                nombre = "Cyber Quest RPG",
                descripcion = "RPG en una metrópolis ciberpunk llena de misiones y combates por turnos.",
                categoria = "RPG",
                version = "2.1.0",
                creadorNombre = "GamerPro_99",
                creadorId = 2,
                estado = EstadoJuego.APROBADO
            ),
            Juego(
                id = 4,
                nombre = "Mind Master Puzzle",
                descripcion = "Más de 100 acertijos lógicos para desafiar tu cerebro y memoria.",
                categoria = "Puzle",
                version = "1.0.5",
                creadorNombre = "DevMaster",
                creadorId = 3,
                estado = EstadoJuego.APROBADO
            ),
            Juego(
                id = 5,
                nombre = "Speed Turbo Racing",
                descripcion = "Carreras de alta velocidad con físicas realistas y multijugador.",
                categoria = "Carreras",
                version = "0.9.0",
                creadorNombre = "SpeedRacer",
                creadorId = 4,
                estado = EstadoJuego.EN_REVISION
            )
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

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedOption) {
                "JUGAR" -> {
                    JugarScreen(
                        juegos = juegosList,
                        usuarioActual = usuario
                    )
                }
                "CREAR" -> {
                    // Pantalla en blanco
                }
                "ESTADISTICAS" -> {
                    EstadisticasScreen(
                        usuario = usuario,
                        puntuacion = puntuacion,
                        nivel = nivel,
                        tiempoJuego = tiempoJuego,
                        isPremium = isPremium,
                        onPuntuacionChange = { puntuacion = it },
                        onNivelChange = { nivel = it },
                        onTiempoChange = { tiempoJuego = it },
                        onPremiumChange = { isPremium = it }
                    )
                }
            }
        }
    }
}

@Composable
fun JugarScreen(
    juegos: List<Juego>,
    usuarioActual: Usuario
) {
    var filtroSeleccionado by remember { mutableStateOf("TODOS") }
    var juegoSeleccionadoDetalle by remember { mutableStateOf<Juego?>(null) }
    var juegoSeleccionadoParaJugar by remember { mutableStateOf<Juego?>(null) }

    val juegosFiltrados = remember(juegos, filtroSeleccionado) {
        when (filtroSeleccionado) {
            "MIS_JUEGOS" -> juegos.filter { it.creadorId == usuarioActual.id }
            "COMUNIDAD" -> juegos.filter { it.creadorId != usuarioActual.id }
            else -> juegos
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Catálogo de Juegos Disponibles",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Explora las características de los juegos creados por ti y por la comunidad.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filtros rápidos
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = filtroSeleccionado == "TODOS",
                    onClick = { filtroSeleccionado = "TODOS" },
                    label = { Text("Todos (${juegos.size})") }
                )
            }
            item {
                FilterChip(
                    selected = filtroSeleccionado == "MIS_JUEGOS",
                    onClick = { filtroSeleccionado = "MIS_JUEGOS" },
                    label = { Text("Mis Creados (${juegos.count { it.creadorId == usuarioActual.id }})") }
                )
            }
            item {
                FilterChip(
                    selected = filtroSeleccionado == "COMUNIDAD",
                    onClick = { filtroSeleccionado = "COMUNIDAD" },
                    label = { Text("Comunidad (${juegos.count { it.creadorId != usuarioActual.id }})") }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (juegosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay juegos en esta categoría.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(juegosFiltrados, key = { it.id }) { juego ->
                    JuegoCard(
                        juego = juego,
                        esCreadoPorMi = juego.creadorId == usuarioActual.id,
                        onVerDetalles = { juegoSeleccionadoDetalle = juego },
                        onJugarClick = { juegoSeleccionadoParaJugar = juego }
                    )
                }
            }
        }
    }

    // Modal de Detalles del Juego
    juegoSeleccionadoDetalle?.let { juego ->
        AlertDialog(
            onDismissRequest = { juegoSeleccionadoDetalle = null },
            title = {
                Text(
                    text = "🎮 ${juego.nombre}",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (juego.creadorId == usuarioActual.id) "⭐ Creado por ti (${juego.creadorNombre})" else "👤 Creador: ${juego.creadorNombre}",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("🏷️ Categoría: ${juego.categoria}")
                    Text("📌 Versión: v${juego.version}")
                    Text("🚦 Estado: ${juego.estado}")
                    Text("📅 Fecha: ${juego.fechaPublicacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Text("📝 Descripción:", fontWeight = FontWeight.Bold)
                    Text(juego.descripcion, fontSize = 14.sp)
                }
            },
            confirmButton = {
                Button(onClick = {
                    val juegoTemporal = juego
                    juegoSeleccionadoDetalle = null
                    juegoSeleccionadoParaJugar = juegoTemporal
                }) {
                    Text("Intentar Jugar")
                }
            },
            dismissButton = {
                TextButton(onClick = { juegoSeleccionadoDetalle = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Modal informativo de intento de juego
    juegoSeleccionadoParaJugar?.let { juego ->
        AlertDialog(
            onDismissRequest = { juegoSeleccionadoParaJugar = null },
            title = { Text("🕹️ ${juego.nombre}") },
            text = {
                Column {
                    Text("Estás explorando las características de este juego.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aún no es ejecutable ya que el motor de renderizado de juegos se implementará más adelante.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { juegoSeleccionadoParaJugar = null }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
fun JuegoCard(
    juego: Juego,
    esCreadoPorMi: Boolean,
    onVerDetalles: () -> Unit,
    onJugarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esCreadoPorMi) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = juego.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                SuggestionChip(
                    onClick = { },
                    label = {
                        Text(
                            text = if (esCreadoPorMi) "Tuyo" else juego.creadorNombre,
                            fontSize = 11.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { },
                    label = { Text(juego.categoria, fontSize = 11.sp) }
                )
                AssistChip(
                    onClick = { },
                    label = { Text("v${juego.version}", fontSize = 11.sp) }
                )
                AssistChip(
                    onClick = { },
                    label = { Text("${juego.estado}", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = juego.descripcion,
                fontSize = 14.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onVerDetalles,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("🔍 Características")
                }

                Button(onClick = onJugarClick) {
                    Text("🎮 Jugar")
                }
            }
        }
    }
}

@Composable
fun EstadisticasScreen(
    usuario: Usuario,
    puntuacion: Int,
    nivel: Int,
    tiempoJuego: Long,
    isPremium: Boolean,
    onPuntuacionChange: (Int) -> Unit,
    onNivelChange: (Int) -> Unit,
    onTiempoChange: (Long) -> Unit,
    onPremiumChange: (Boolean) -> Unit
) {
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
                Text("Fecha de Registro: ${usuario.fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}")
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
                val nuevaPuntuacion = puntuacion + 100
                onPuntuacionChange(nuevaPuntuacion)
                if (nuevaPuntuacion >= nivel * 100) {
                    onNivelChange(nivel + 1)
                }
            }) {
                Text("+100 Pts")
            }

            OutlinedButton(onClick = {
                onTiempoChange(tiempoJuego + 30)
            }) {
                Text("+30m Tiempo")
            }

            FilterChip(
                selected = isPremium,
                onClick = { onPremiumChange(!isPremium) },
                label = { Text(if (isPremium) "Premium" else "Normal") }
            )
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
