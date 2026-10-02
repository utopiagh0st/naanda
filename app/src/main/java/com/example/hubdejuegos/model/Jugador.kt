package com.example.hubdejuegos.model

open class Jugador(
    val id: Int,
    val usuario: Usuario,
    var puntuacion: Int = 0,
    var nivel: Int = 1
) {
    fun unirse(partida: Partida) {
        partida.iniciarPartida()
    }

    fun abandonar() {
        // Lógica para abandonar partida
    }

    fun actualizarPuntuacion(puntos: Int) {
        puntuacion += puntos
        if (puntuacion >= nivel * 100) {
            nivel++
        }
    }
}

class JugadorNormal(
    id: Int,
    usuario: Usuario,
    puntuacion: Int = 0,
    nivel: Int = 1,
    var tiempoJuegoTotal: Long = 0L // En minutos
) : Jugador(id, usuario, puntuacion, nivel) {

    fun obtenerEstadisticas(): String {
        return "Puntuación: $puntuacion | Nivel: $nivel | Tiempo de juego: ${tiempoJuegoTotal}m"
    }
}

class JugadorPremium(
    id: Int,
    usuario: Usuario,
    puntuacion: Int = 0,
    nivel: Int = 1,
    val beneficios: MutableList<String> = mutableListOf("Acceso anticipado", "Soporte VIP", "Skins exclusivas")
) : Jugador(id, usuario, puntuacion, nivel) {

    fun obtenerBeneficios(): String {
        return beneficios.joinToString(separator = ", ")
    }

    fun renovarSuscripcion() {
        // Lógica de renovación de suscripción
    }
}
