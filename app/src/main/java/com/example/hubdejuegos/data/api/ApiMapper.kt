package com.example.hubdejuegos.data.api

object ApiMapper {
    fun mapTriviaToSummary(dto: TriviaDto): String {
        return "Pregunta (${dto.category}): ${dto.question} [Correcta: ${dto.correctAnswer}]"
    }
}
