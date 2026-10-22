package com.example.hubdejuegos.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hubdejuegos.model.Juego

@Entity(tableName = "juegos_favoritos")
data class JuegoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val juegoId: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val version: String,
    val imagenPortada: String
) {
    fun toJuego(): Juego {
        return Juego(
            id = juegoId,
            nombre = nombre,
            descripcion = descripcion,
            categoria = categoria,
            version = version,
            imagenPortada = imagenPortada
        )
    }

    companion object {
        fun fromJuego(juego: Juego): JuegoEntity {
            return JuegoEntity(
                juegoId = juego.id,
                nombre = juego.nombre,
                descripcion = juego.descripcion,
                categoria = juego.categoria,
                version = juego.version,
                imagenPortada = juego.imagenPortada
            )
        }
    }
}
