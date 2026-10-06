package com.example.hubdejuegos.model

import java.time.LocalDateTime

class Juego(
    val id: Int,
    var nombre: String,
    var descripcion: String,
    var categoria: String,
    var version: String,
    var imagenPortada: String = "",
    val fechaPublicacion: LocalDateTime = LocalDateTime.now()
) {
    fun iniciar() {
        // Lógica para iniciar juego
    }

    fun actualizar() {
        // Lógica para actualizar juego
    }

    fun eliminar() {
        // Lógica para eliminar juego
    }

    fun obtenerInfo(): String {
        return "Juego: $nombre v$version - Categoría: $categoria - Portada: $imagenPortada\nDescripción: $descripcion"
    }
}
