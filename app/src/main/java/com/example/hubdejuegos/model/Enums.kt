package com.example.hubdejuegos.model

enum class Rol {
    JUGADOR,
    CREADOR,
    ADMIN
}

enum class EstadoJuego {
    EN_REVISION,
    APROBADO,
    RECHAZADO,
    ELIMINADO
}

enum class EstadoPartida {
    ESPERA,
    EN_CURSO,
    FINALIZADA,
    CANCELADA
}
