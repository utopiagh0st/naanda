package com.example.instagram.model

class ArchivoJuego(
    val id: Int,
    var nombreArchivo: String,
    var ruta: String,
    var tamano: Long,
    var tipo: String
) {
    fun cargar() {
        // Lógica para cargar archivo
    }

    fun descargar() {
        // Lógica para descargar archivo
    }

    fun validar(): Boolean {
        return tamano > 0 && nombreArchivo.isNotBlank()
    }
}
