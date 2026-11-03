package com.example.hubdejuegos

import android.Manifest
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hubdejuegos.model.*
import com.example.hubdejuegos.data.JuegoDao
import com.example.hubdejuegos.data.JuegoEntity
import com.example.hubdejuegos.data.LeaderboardUser
import com.example.hubdejuegos.ui.screens.EstadisticasScreen
import com.example.hubdejuegos.ui.screens.FavoritosScreen
import com.example.hubdejuegos.ui.screens.JugarScreen
import com.example.hubdejuegos.ui.screens.LeaderboardDialog
import com.example.hubdejuegos.ui.screens.LoginScreen
import com.example.hubdejuegos.ui.screens.TriviaScreen
import com.example.hubdejuegos.ui.theme.HubDeJuegosTheme
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HubDeJuegosTheme {
                var currentUser by remember { mutableStateOf<Usuario?>(null) }
                val context = LocalContext.current
                val juegoDao = remember { JuegoDao(context) }

                if (currentUser == null) {
                    LoginScreen(
                        juegoDao = juegoDao,
                        onLoginSuccess = { user -> currentUser = user }
                    )
                } else {
                    MainScreen(
                        currentUser = currentUser!!,
                        onLogout = { currentUser = null }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentUser: Usuario,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Jugar, 1: Favoritos, 2: Trivia, 3: Crear, 4: Estadísticas

    val context = LocalContext.current
    val juegoDao = remember { JuegoDao(context) }
    val scope = rememberCoroutineScope()
    var favoritosDb by remember { mutableStateOf<List<JuegoEntity>>(emptyList()) }
    var comunidadGames by remember { mutableStateOf<List<Pair<JuegoEntity, String>>>(emptyList()) }
    var cloudError by remember { mutableStateOf<String?>(null) }
    var showLeaderboard by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(currentUser.id) {
        scope.launch(Dispatchers.IO) {
            try {
                val loadedFavs = juegoDao.obtenerFavoritos(currentUser.id)
                val loadedComunidad = juegoDao.obtenerJuegosComunidad()
                withContext(Dispatchers.Main) {
                    favoritosDb = loadedFavs
                    comunidadGames = loadedComunidad
                    cloudError = null
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    cloudError = "Error al cargar datos de la nube: ${e.localizedMessage}"
                }
            }
        }
    }

    val favoritosIds = remember(favoritosDb) { favoritosDb.map { it.juegoId }.toSet() }

    var puntuacion by remember { mutableIntStateOf(450) }
    var nivel by remember { mutableIntStateOf(5) }
    var tiempoJuego by remember { mutableLongStateOf(120L) }
    var isPremium by remember { mutableStateOf(false) }

    // Juegos iniciales de ejemplo + los creados por la comunidad en la nube
    val juegosUiList = remember(comunidadGames) {
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
        val comItems = comunidadGames.map { (entity, autorNombre) ->
            JuegoUiItem(
                juego = entity.toJuego(),
                icono = "🕹️",
                autor = autorNombre,
                calificacion = 5.0f,
                partidasJugadas = 1
            )
        }
        comItems + baseJuegos
    }

    val favoritosUiItems = remember(favoritosDb, juegosUiList) {
        favoritosDb.map { fav ->
            juegosUiList.find { it.juego.id == fav.juegoId } ?: JuegoUiItem(
                juego = fav.toJuego(),
                icono = "⭐",
                autor = "Usuario",
                calificacion = 5.0f,
                partidasJugadas = 1
            )
        }
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
                                text = "HubJuegos (${currentUser.nombre})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = when (selectedTab) {
                                    0 -> "Catálogo de Juegos"
                                    1 -> "Mis Favoritos"
                                    2 -> "Trivia de Videojuegos"
                                    3 -> "Creador de Juegos"
                                    else -> "Perfil y Estadísticas"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showLeaderboard = true }) {
                        Text("🏆", fontSize = 20.sp)
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("⭐", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$puntuacion",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
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
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                    label = { Text("Favoritos") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab == 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Trivia") },
                    label = { Text("Trivia") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Crear") },
                    label = { Text("Crear") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Estadísticas") },
                    label = { Text("Estadísticas") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (cloudError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠️ $cloudError",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { cloudError = null }) {
                            Text("Cerrar", color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> {
                        JugarScreen(
                            juegos = juegosUiList,
                            favoritosIds = favoritosIds,
                            juegoDao = juegoDao,
                            currentUser = currentUser,
                            onJuegoIniciado = { _ ->
                                puntuacion += 10
                                tiempoJuego += 15
                                if (puntuacion >= nivel * 100) nivel++
                            },
                            onToggleFavorito = { item ->
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val isFav = favoritosIds.contains(item.juego.id)
                                        if (isFav) {
                                            juegoDao.eliminarFavoritoPorJuegoId(currentUser.id, item.juego.id)
                                        } else {
                                            juegoDao.insertarFavorito(currentUser.id, JuegoEntity.fromJuego(item.juego))
                                        }
                                        val updated = juegoDao.obtenerFavoritos(currentUser.id)
                                        withContext(Dispatchers.Main) {
                                            favoritosDb = updated
                                            cloudError = null
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            cloudError = "Error al actualizar favorito: ${e.localizedMessage}"
                                        }
                                    }
                                }
                            }
                        )
                    }
                    1 -> {
                        FavoritosScreen(
                            favoritos = favoritosUiItems,
                            onJuegoIniciado = { _ ->
                                puntuacion += 10
                                tiempoJuego += 15
                                if (puntuacion >= nivel * 100) nivel++
                            },
                            onToggleFavorito = { item ->
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        juegoDao.eliminarFavoritoPorJuegoId(currentUser.id, item.juego.id)
                                        val updated = juegoDao.obtenerFavoritos(currentUser.id)
                                        withContext(Dispatchers.Main) {
                                            favoritosDb = updated
                                            cloudError = null
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            cloudError = "Error al eliminar favorito: ${e.localizedMessage}"
                                        }
                                    }
                                }
                            }
                        )
                    }
                    2 -> {
                        TriviaScreen(
                            onCorrectAnswer = { pts ->
                                puntuacion += pts
                                if (puntuacion >= nivel * 100) nivel++
                            }
                        )
                    }
                    3 -> {
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
                                                scope.launch(Dispatchers.IO) {
                                                    try {
                                                        val nuevoJuego = Juego(
                                                            id = 0,
                                                            nombre = nombreJuego,
                                                            descripcion = descripcionJuego,
                                                            categoria = categoriaJuego,
                                                            version = versionJuego,
                                                            imagenPortada = imagenPortada
                                                        )
                                                        val entity = JuegoEntity.fromJuego(nuevoJuego)
                                                        juegoDao.publicarJuegoComunidad(currentUser.id, entity)
                                                        val updatedComunidad = juegoDao.obtenerJuegosComunidad()
                                                        withContext(Dispatchers.Main) {
                                                            comunidadGames = updatedComunidad
                                                            mensajeExito = "¡Juego '${nuevoJuego.nombre}' publicado en la nube para la comunidad!"
                                                            puntuacion += 50 // Recompensa por crear un juego
                                                            if (puntuacion >= nivel * 100) nivel++

                                                            // Reset form
                                                            nombreJuego = ""
                                                            descripcionJuego = ""
                                                            categoriaJuego = "Acción"
                                                            versionJuego = "1.0.0"
                                                            imagenPortada = ""
                                                            cloudError = null
                                                        }
                                                    } catch (e: Exception) {
                                                        withContext(Dispatchers.Main) {
                                                            cloudError = "Error al publicar juego: ${e.localizedMessage}"
                                                        }
                                                    }
                                                }
                                            } else {
                                                mensajeExito = "Por favor, ingresa al menos el nombre del juego."
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Publicar en la Nube")
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
                        }
                    }
                4 -> {
                    EstadisticasScreen(
                        usuario = currentUser,
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

    if (showLeaderboard) {
         LeaderboardDialog(
            juegoDao = juegoDao,
            onDismiss = { showLeaderboard = false }
        )
    }
}
}
