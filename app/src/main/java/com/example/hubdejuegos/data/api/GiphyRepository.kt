package com.example.hubdejuegos.data.api

class GiphyRepository(private val client: GiphyClient = GiphyClient()) {
    suspend fun searchGifs(query: String): List<GiphyDto> {
        return client.fetchGifs(query)
    }
}
