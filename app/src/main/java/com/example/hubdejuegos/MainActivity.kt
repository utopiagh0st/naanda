package com.example.hubdejuegos

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hubdejuegos.model.*
import com.example.hubdejuegos.ui.screens.EstadisticasScreen
import com.example.hubdejuegos.ui.screens.JugarScreen
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
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Jugar, 1: Crear, 2: Estadisticas

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

    // Lista de juegos creados y catálogo
    val listaJuegos = remember { mutableStateOf(mutableListOf<Juego>()) }
    
    // Juegos iniciales de ejemplo + los creados por el usuario
    val juegosUiList = remember(listaJuegos.value) {
        val baseJuegos = listOf(
            JuegoUiItem(
                juego = Juego(1, "Super Runner", "Corre y salva el mundo en este juego de plataforma frenético.", "Acción", "1.2.0"),
                icono = "🏃",
                autor = "Rodo",
                calificacion = 4.9f,
                partidasJugadas = 1250
            ),
            JuegoUiItem(
                juego = Juego(2, "Puzzle Master", "Resuelve complejos rompecabezas lógicos y entrena tu mente.", "Puzzle", "1.0.0"),
                icono = "🧩",
                autor = "Juli",
                calificacion = 4.7f,
                partidasJugadas = 840
            ),
            JuegoUiItem(
                juego = Juego(3, "Space Battle", "Combates espaciales épicos contra flotas alienígenas.", "Estrategia", "2.1.0"),
                icono = "🚀",
                autor = "Comunidad",
                calificacion = 4.8f,
                partidasJugadas = 2100
            )
        )
        // Mapear los juegos creados por el usuario a JuegoUiItem
        val userJuegos = listaJuegos.value.map { j ->
            JuegoUiItem(
                juego = j,
                icono = "🕹️",
                autor = usuario.nombre,
                calificacion = 5.0f,
                partidasJugadas = 1
            )
        }
        userJuegos + baseJuegos
    }

    // Estados para la creación de juegos
    var nombreJuego by remember { mutableStateOf("") }
    var descripcionJuego by remember { mutableStateOf("") }
    var categoriaJuego by remember { mutableStateOf("Acción") }
    var versionJuego by remember { mutableStateOf("1.0.0") }
    var imagenPortada by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }

    val categoriasDisponibles = listOf("Acción", "Aventura", "Puzzle", "Estrategia", "RPG", "Deportes", "Simulación", "Arcade")

    // Launcher para galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imagenPortada = uri.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text("🎮", fontSize = 18.sp, modifier = Modifier.padding(6.dp))
                        }

                        Column {
                            Text(
                                text = "HubJuegos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = when (selectedTab) {
                                    0 -> "Catálogo de Juegos"
                                    1 -> "Creador de Juegos"
                                    else -> "Perfil y Estadísticas"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("⭐", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$puntuacion pts",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Jugar") },
                    label = { Text("Jugar") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Crear") },
                    label = { Text("Crear") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Estadísticas") },
                    label = { Text("Estadísticas") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> {
                    JugarScreen(
                        juegos = juegosUiList,
                        onJuegoIniciado = { _ ->
                            puntuacion += 10
                            tiempoJuego += 15
                            if (puntuacion >= nivel * 100) nivel++
                        }
                    )
                }
                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = RoundedCornerShape(20.dp)
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
                                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Seleccionar Imagen de la Galería")
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        if (nombreJuego.isNotBlank()) {
                                            val nuevoJuego = Juego(
                                                id = listaJuegos.value.size + 10,
                                                nombre = nombreJuego,
                                                descripcion = descripcionJuego,
                                                categoria = categoriaJuego,
                                                version = versionJuego,
                                                imagenPortada = imagenPortada
                                            )
                                            val mutableList = listaJuegos.value.toMutableList()
                                            mutableList.add(0, nuevoJuego)
                                            listaJuegos.value = mutableList

                                            mensajeExito = "¡Juego '${nuevoJuego.nombre}' creado con éxito y añadido a Jugar!"
                                            puntuacion += 50 // Recompensa por crear un juego
                                            if (puntuacion >= nivel * 100) nivel++

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
                                    Text("Guardar y Publicar Juego")
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

                        if (listaJuegos.value.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("📋 Tus Juegos Creados (${listaJuegos.value.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    listaJuegos.value.forEach { juego ->
                                        Text("• ${juego.nombre} (v${juego.version}) - Cat: ${juego.categoria}")
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    EstadisticasScreen(
                        usuario = usuario,
                        puntuacion = puntuacion,
                        nivel = nivel,
                        tiempoJuego = tiempoJuego,
                        isPremium = isPremium,
                        onPuntuacionAdd = { pts ->
                            puntuacion += pts
                            if (puntuacion >= nivel * 100) nivel++
                        },
                        onTiempoAdd = { mins ->
                            tiempoJuego += mins
                        },
                        onTogglePremium = {
                            isPremium = !isPremium
                        }
                    )
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
