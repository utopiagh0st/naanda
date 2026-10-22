package com.example.hubdejuegos.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.room.Database

@Database(entities = [JuegoEntity::class], version = 1)
class JuegoDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "hub_juegos_room.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE juegos_favoritos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                juegoId INTEGER NOT NULL,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                categoria TEXT,
                version TEXT,
                imagenPortada TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS juegos_favoritos")
        onCreate(db)
    }
}
