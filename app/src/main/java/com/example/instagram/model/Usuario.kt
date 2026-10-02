package com.example.instagram.model

import java.time.LocalDateTime

open class Usuario(
    val id: Int,
    var nombre: String,
    var email: String,
    var contrasena: String,
    val fechaRegistro: LocalDateTime = LocalDateTime.now(),
    var rol: Rol = Rol.JUGADOR
) {
    fun iniciarSesion(email: String, contrasena: String): Boolean {
        return this.email == email && this.contrasena == contrasena
    }

    fun cerrarSesion() {
        // Lógica de cerrar sesión
    }

    fun subirJuego(juego: Juego) {
        // Lógica de subir juego
    }

    fun jugarJuego(juego: Juego): Partida {
        return Partida(
            id = (1..1000).random(),
            fechaInicio = LocalDateTime.now(),
            estado = EstadoPartida.EN_CURSO
        )
    }

    fun editarPerfil() {
        // Lógica de editar perfil
    }

    fun eliminarCuenta() {
        // Lógica de eliminar cuenta
    }
}
