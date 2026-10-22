package com.example.hubdejuegos.data.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class GiphyClient {
    companion object {
        private const val API_KEY = "2FFGHXGMGfCkMvRjVF2cI7G9cTQ0kXAJ"
        private const val BASE_URL = "https://api.giphy.com/v1/gifs/search"
    }

    suspend fun fetchGifs(query: String): List<GiphyDto> = withContext(Dispatchers.IO) {
        val result = mutableListOf<GiphyDto>()
        try {
            val urlString = "$BASE_URL?api_key=$API_KEY&q=${URLEncoder.encode(query, "UTF-8")}&limit=10"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(responseString)
                val dataArray = jsonObject.getJSONArray("data")

                for (i in 0 until dataArray.length()) {
                    val item = dataArray.getJSONObject(i)
                    val id = item.getString("id")
                    val title = item.optString("title", "GIF")
                    val images = item.getJSONObject("images")
                    val fixedHeight = images.getJSONObject("fixed_height")
                    val imageUrl = fixedHeight.getString("url")

                    result.add(GiphyDto(id, title, imageUrl))
                }
            }
        } catch (e: Exception) {
            Log.e("GiphyClient", "Error fetching GIFs", e)
        }
        result
    }
}
