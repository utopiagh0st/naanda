package com.example.hubdejuegos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hubdejuegos.model.JuegoUiItem

@Composable
fun FavoritosScreen(
    favoritos: List<JuegoUiItem>,
    onJuegoIniciado: (JuegoUiItem) -> Unit,
    onToggleFavorito: (JuegoUiItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("❤️ Mis Juegos Favoritos (${favoritos.size})", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (favoritos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💔", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No tienes juegos favoritos aún", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Marca con un corazón los juegos en la pestaña Jugar", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(favoritos) { item ->
                    JuegoCard(
                        juegoItem = item,
                        isFavorito = true,
                        onClick = { },
                        onPlayClick = { onJuegoIniciado(item) },
                        onToggleFavorito = { onToggleFavorito(item) }
                    )
                }
            }
        }
    }
}
