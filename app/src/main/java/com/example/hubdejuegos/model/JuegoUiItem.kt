package com.example.hubdejuegos.model

data class JuegoUiItem(
    val juego: Juego,
    val icono: String = "🎮",
    val autor: String = "Comunidad",
    val calificacion: Float = 4.8f,
    val votos: Int = 120,
    val partidasJugadas: Int = 450
)
