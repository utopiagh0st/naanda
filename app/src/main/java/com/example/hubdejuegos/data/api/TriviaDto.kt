package com.example.hubdejuegos.data.api

data class TriviaDto(
    val category: String,
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
) {
    val allOptions: List<String>
        get() = (incorrectAnswers + correctAnswer).shuffled()
}
