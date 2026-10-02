package com.example.hubdejuegos.model

import java.time.LocalDateTime

class Resena(
    val id: Int,
    var puntuacion: Int,
    var comentario: String,
    val fecha: LocalDateTime = LocalDateTime.now()
) {
    fun publicar() {
        // Lógica para publicar reseña
    }

    fun editar() {
        // Lógica para editar reseña
    }

    fun eliminar() {
        // Lógica para eliminar reseña
    }
}
