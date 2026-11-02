package com.example.hubdejuegos.data

data class ResenaEntity(
    val id: Int = 0,
    val juegoId: Int,
    val usuarioId: Int,
    val autorNombre: String,
    val puntuacion: Int,
    val comentario: String,
    val gifUrl: String,
    val fecha: String
)
