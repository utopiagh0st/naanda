package com.example.hubdejuegos.data.api

class TriviaRepository(private val client: TriviaClient = TriviaClient()) {
    suspend fun getTriviaQuestions(): List<TriviaDto> {
        return client.fetchTriviaQuestions()
    }
}
