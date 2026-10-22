package com.example.hubdejuegos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hubdejuegos.data.api.GiphyRepository
import com.example.hubdejuegos.data.api.GiphyDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiphyScreen() {
    val repository = remember { GiphyRepository() }
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("gaming") }
    var gifs by remember { mutableStateOf<List<GiphyDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    fun search(query: String) {
        scope.launch {
            isLoading = true
            gifs = repository.searchGifs(query)
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        search("gaming")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("🎞️ Explorador de GIFs de Juegos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Busca y descubre GIFs animados con la API de Giphy", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar GIFs (ej. mario, zelda, gaming)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            trailingIcon = {
                Button(
                    onClick = { search(searchQuery) },
                    modifier = Modifier.padding(end = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Buscar")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (gifs.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No se encontraron GIFs")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(gifs) { gif ->
                    Card(
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = gif.imageUrl,
                                contentDescription = gif.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}
