package com.example.hubdejuegos.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.example.hubdejuegos.model.Usuario
import com.example.hubdejuegos.model.Rol
import java.time.LocalDateTime

data class LeaderboardUser(
    val id: Int,
    val nombre: String,
    val puntuacion: Int,
    val nivel: Int
)

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

    fun registrarUsuario(nombre: String, email: String, contrasena: String): Usuario? = runBlocking(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "register")
                put("nombre", nombre)
                put("email", email)
                put("contrasena", contrasena)
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(responseString)
                val dataObj = jsonObj.optJSONObject("data") ?: jsonObj.optJSONObject("result")
                val success = dataObj?.optBoolean("success") ?: false
                if (success) {
                    val userObj = dataObj?.optJSONObject("user")
                    val id = userObj?.optInt("id") ?: 1
                    val name = userObj?.optString("nombre") ?: nombre
                    val mail = userObj?.optString("email") ?: email
                    return@runBlocking Usuario(id = id, nombre = name, email = mail, contrasena = "", rol = Rol.JUGADOR)
                }
            }
        } catch (e: Exception) {
            Log.e("JuegoDao", "Error registering user", e)
        }
        return@runBlocking null
    }

    fun loginUsuario(email: String, contrasena: String): Usuario? = runBlocking(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "login")
                put("email", email)
                put("contrasena", contrasena)
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(responseString)
                val dataObj = jsonObj.optJSONObject("data") ?: jsonObj.optJSONObject("result")
                val success = dataObj?.optBoolean("success") ?: false
                if (success) {
                    val userObj = dataObj?.optJSONObject("user")
                    val id = userObj?.optInt("id") ?: 1
                    val name = userObj?.optString("nombre") ?: "Usuario"
                    val mail = userObj?.optString("email") ?: email
                    return@runBlocking Usuario(id = id, nombre = name, email = mail, contrasena = "", rol = Rol.JUGADOR)
                }
            }
        } catch (e: Exception) {
            Log.e("JuegoDao", "Error logging in", e)
        }
        return@runBlocking null
    }

    fun insertarFavorito(usuarioId: Int, juego: JuegoEntity): Long = runBlocking(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "insert_favorito")
                put("usuarioId", usuarioId)
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

    fun obtenerFavoritos(usuarioId: Int): List<JuegoEntity> = runBlocking(Dispatchers.IO) {
        val lista = mutableListOf<JuegoEntity>()
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "get_favoritos")
                put("usuarioId", usuarioId)
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

    fun eliminarFavoritoPorJuegoId(usuarioId: Int, juegoId: Int): Int = runBlocking(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "delete_favorito")
                put("usuarioId", usuarioId)
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

    fun obtenerJuegosComunidad(): List<Pair<JuegoEntity, String>> = runBlocking(Dispatchers.IO) {
        val lista = mutableListOf<Pair<JuegoEntity, String>>()
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "get_comunidad_juegos")
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val rows = parseRowsFromJson(responseString)
                for (i in 0 until rows.length()) {
                    val row = rows.optJSONObject(i) ?: continue
                    val id = row.optInt("id", i + 500)
                    val nombre = row.optString("nombre", "")
                    val descripcion = row.optString("descripcion", "")
                    val categoria = row.optString("categoria", "")
                    val version = row.optString("version", "1.0.0")
                    val imagenPortada = row.optString("imagenPortada", row.optString("imagenportada", ""))
                    val autor = row.optString("autorNombre", row.optString("autornombre", "Comunidad"))

                    val entity = JuegoEntity(id = id, juegoId = id + 1000, nombre = nombre, descripcion = descripcion, categoria = categoria, version = version, imagenPortada = imagenPortada)
                    lista.add(Pair(entity, autor))
                }
            }
        } catch (e: Exception) {
            Log.e("JuegoDao", "Error fetching community games", e)
        }
        return@runBlocking lista
    }

    fun publicarJuegoComunidad(usuarioId: Int, juego: JuegoEntity): Long = runBlocking(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "insert_comunidad_juego")
                put("usuarioId", usuarioId)
                put("nombre", juego.nombre)
                put("descripcion", juego.descripcion)
                put("categoria", juego.categoria)
                put("version", juego.version)
                put("imagenPortada", juego.imagenPortada)
                put("fechaPublicacion", LocalDateTime.now().toString())
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
            Log.e("JuegoDao", "Error publishing community game", e)
            throw e
        }
        return@runBlocking -1L
    }

    fun obtenerResenas(juegoId: Int): List<ResenaEntity> = runBlocking(Dispatchers.IO) {
        val lista = mutableListOf<ResenaEntity>()
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "get_resenas")
                put("juegoId", juegoId)
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val rows = parseRowsFromJson(responseString)
                for (i in 0 until rows.length()) {
                    val row = rows.optJSONObject(i) ?: continue
                    val id = row.optInt("id", i + 1)
                    val jId = row.optInt("juegoId", row.optInt("juegoid", juegoId))
                    val uId = row.optInt("usuarioId", row.optInt("usuarioid", 1))
                    val autor = row.optString("autorNombre", row.optString("autornombre", "Usuario"))
                    val puntos = row.optInt("puntuacion", 5)
                    val com = row.optString("comentario", "")
                    val gif = row.optString("gifUrl", row.optString("gifurl", ""))
                    val fecha = row.optString("fecha", "")

                    lista.add(ResenaEntity(id = id, juegoId = jId, usuarioId = uId, autorNombre = autor, puntuacion = puntos, comentario = com, gifUrl = gif, fecha = fecha))
                }
            }
        } catch (e: Exception) {
            Log.e("JuegoDao", "Error fetching reviews", e)
        }
        return@runBlocking lista
    }

    fun insertarResena(resena: ResenaEntity): Long = runBlocking(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "insert_resena")
                put("juegoId", resena.juegoId)
                put("usuarioId", resena.usuarioId)
                put("autorNombre", resena.autorNombre)
                put("puntuacion", resena.puntuacion)
                put("comentario", resena.comentario)
                put("gifUrl", resena.gifUrl)
                put("fecha", resena.fecha)
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return@runBlocking 1L
            }
        } catch (e: Exception) {
            Log.e("JuegoDao", "Error inserting review", e)
            throw e
        }
        return@runBlocking -1L
    }

    fun obtenerRanking(): List<LeaderboardUser> = runBlocking(Dispatchers.IO) {
        val lista = mutableListOf<LeaderboardUser>()
        try {
            val url = URL("$BASE_URL?apikey=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            setupConnection(connection)

            val jsonBody = JSONObject().apply {
                put("action", "get_leaderboard")
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val rows = parseRowsFromJson(responseString)
                for (i in 0 until rows.length()) {
                    val row = rows.optJSONObject(i) ?: continue
                    val id = row.optInt("id", i + 1)
                    val nombre = row.optString("nombre", "Usuario")
                    val puntuacion = row.optInt("puntuacion", 450)
                    val nivel = row.optInt("nivel", 5)

                    lista.add(LeaderboardUser(id = id, nombre = nombre, puntuacion = puntuacion, nivel = nivel))
                }
            }
        } catch (e: Exception) {
            Log.e("JuegoDao", "Error fetching leaderboard", e)
        }
        return@runBlocking lista
    }

    fun esFavorito(usuarioId: Int, juegoId: Int): Boolean {
        return try {
            val favoritos = obtenerFavoritos(usuarioId)
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
