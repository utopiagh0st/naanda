package com.example.hubdejuegos.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class JuegoDao(context: Context) {
    companion object {
        private const val API_KEY = "obAA4sjO40MEanvtiCMjr1VQeK2Vxapq3ATw8IXaKAU"
        private const val BASE_URL = "https://cvymr9oqvz.g6.gateway.sqlite.cloud/v2/functions/fav"
    }

    private fun setupConnection(connection: HttpURLConnection) {
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $API_KEY")
        connection.setRequestProperty("apikey", API_KEY)
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        connection.doOutput = true
    }

    fun insertarFavorito(juego: JuegoEntity): Long = runBlocking(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "insert")
                put("juegoId", juego.juegoId)
                put("nombre", juego.nombre)
                put("descripcion", juego.descripcion)
                put("categoria", juego.categoria)
                put("version", juego.version)
                put("imagenPortada", juego.imagenPortada)
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return@runBlocking 1L
            } else {
                val errorString = try { connection.errorStream?.bufferedReader()?.use { it.readText() } } catch(_: Exception) { "" }
                throw Exception("HTTP $responseCode: $errorString")
            }
        } catch (e: Exception) {
            Log.e("JuegoDao", "Error inserting favorite to cloud", e)
            throw e
        }
    }

    fun obtenerFavoritos(): List<JuegoEntity> = runBlocking(Dispatchers.IO) {
        val lista = mutableListOf<JuegoEntity>()
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST" // Using POST to always send a valid JSON body
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "get")
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d("JuegoDao", "Raw cloud response: $responseString")

                val rows = parseRowsFromJson(responseString)
                for (i in 0 until rows.length()) {
                    val row = rows.optJSONObject(i) ?: continue
                    val id = row.optInt("id", i + 1)
                    val juegoId = row.optInt("juegoId", row.optInt("juegoid", 0))
                    val nombre = row.optString("nombre", "")
                    val descripcion = row.optString("descripcion", "")
                    val categoria = row.optString("categoria", "")
                    val version = row.optString("version", "1.0.0")
                    val imagenPortada = row.optString("imagenPortada", row.optString("imagenportada", ""))

                    if (juegoId > 0) {
                        lista.add(JuegoEntity(id = id, juegoId = juegoId, nombre = nombre, descripcion = descripcion, categoria = categoria, version = version, imagenPortada = imagenPortada))
                    }
                }
            } else {
                val errorString = try { connection.errorStream?.bufferedReader()?.use { it.readText() } } catch(_: Exception) { "" }
                throw Exception("HTTP $responseCode: $errorString")
            }
        } catch (e: Exception) {
            Log.e("JuegoDao", "Error fetching favorites from cloud", e)
            throw e
        }
        return@runBlocking lista
    }

    fun eliminarFavoritoPorJuegoId(juegoId: Int): Int = runBlocking(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST" // Using POST for delete action
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "delete")
                put("juegoId", juegoId)
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return@runBlocking 1
            } else {
                val errorString = try { connection.errorStream?.bufferedReader()?.use { it.readText() } } catch(_: Exception) { "" }
                throw Exception("HTTP $responseCode: $errorString")
            }
        } catch (e: Exception) {
            Log.e("JuegoDao", "Error deleting favorite from cloud", e)
            throw e
        }
    }

    fun esFavorito(juegoId: Int): Boolean {
        return try {
            val favoritos = obtenerFavoritos()
            favoritos.any { it.juegoId == juegoId }
        } catch (_: Exception) {
            false
        }
    }

    private fun parseRowsFromJson(responseString: String): JSONArray {
        val trimmed = responseString.trim()
        if (trimmed.startsWith("[")) {
            return JSONArray(trimmed)
        }
        val jsonObj = JSONObject(trimmed)
        val possibleKeys = listOf("data", "result", "results", "rows", "values")
        for (key in possibleKeys) {
            if (jsonObj.has(key)) {
                val obj = jsonObj.get(key)
                if (obj is JSONArray) return obj
            }
        }
        return JSONArray()
    }
}
