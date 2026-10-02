package com.example.hubdejuegos.model

import java.time.LocalDateTime

class Partida(
    val id: Int,
    val fechaInicio: LocalDateTime = LocalDateTime.now(),
    var fechaFin: LocalDateTime? = null,
    var estado: EstadoPartida = EstadoPartida.ESPERA
) {
    fun iniciarPartida() {
        estado = EstadoPartida.EN_CURSO
    }

    fun finalizarPartida() {
        estado = EstadoPartida.FINALIZADA
        fechaFin = LocalDateTime.now()
    }

    fun obtenerResultados(): String {
        return "Partida #$id - Estado: $estado - Inicio: $fechaInicio - Fin: ${fechaFin ?: "En curso"}"
    }
}
