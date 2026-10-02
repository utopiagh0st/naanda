package com.example.instagram.model

class Categoria(
    val id: Int,
    var nombre: String,
    var descripcion: String
) {
    private val juegos: MutableList<Juego> = mutableListOf()

    fun obtenerJuegos(): List<Juego> {
        return juegos.toList()
    }

    fun editar() {
        // Lógica de editar categoría
    }

    fun eliminar() {
        // Lógica de eliminar categoría
    }
}
