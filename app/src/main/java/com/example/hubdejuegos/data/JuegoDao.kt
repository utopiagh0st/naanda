package com.example.hubdejuegos.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidx.room.Dao

@Dao
class JuegoDao(context: Context) {
    private val dbHelper = JuegoDatabase(context)

    fun insertarFavorito(juego: JuegoEntity): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("juegoId", juego.juegoId)
            put("nombre", juego.nombre)
            put("descripcion", juego.descripcion)
            put("categoria", juego.categoria)
            put("version", juego.version)
            put("imagenPortada", juego.imagenPortada)
        }
        return db.insert("juegos_favoritos", null, values)
    }

    fun obtenerFavoritos(): List<JuegoEntity> {
        val lista = mutableListOf<JuegoEntity>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query("juegos_favoritos", null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val juegoId = cursor.getInt(cursor.getColumnIndexOrThrow("juegoId"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                val version = cursor.getString(cursor.getColumnIndexOrThrow("version"))
                val imagenPortada = cursor.getString(cursor.getColumnIndexOrThrow("imagenPortada"))
                lista.add(JuegoEntity(id, juegoId, nombre, descripcion, categoria, version, imagenPortada))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun eliminarFavoritoPorJuegoId(juegoId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("juegos_favoritos", "juegoId = ?", arrayOf(juegoId.toString()))
    }

    fun esFavorito(juegoId: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query("juegos_favoritos", arrayOf("id"), "juegoId = ?", arrayOf(juegoId.toString()), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}
