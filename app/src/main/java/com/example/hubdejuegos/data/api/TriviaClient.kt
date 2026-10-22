package com.example.hubdejuegos.data.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class TriviaClient {
    companion object {
        // Category 15 is Video Games in Open Trivia DB
        private const val BASE_URL = "https://opentdb.com/api.php?amount=5&category=15&type=multiple"
    }

    suspend fun fetchTriviaQuestions(): List<TriviaDto> = withContext(Dispatchers.IO) {
        val result = mutableListOf<TriviaDto>()
        try {
            val url = URL(BASE_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(responseString)
                val resultsArray = jsonObject.getJSONArray("results")

                for (i in 0 until resultsArray.length()) {
                    val item = resultsArray.getJSONObject(i)
                    val category = item.getString("category")
                    val question = item.getString("question")
                    val correctAnswer = item.getString("correct_answer")
                    val incorrectArray = item.getJSONArray("incorrect_answers")
                    val incorrectList = mutableListOf<String>()
                    for (j in 0 until incorrectArray.length()) {
                        incorrectList.add(incorrectArray.getString(j))
                    }

                    result.add(TriviaDto(category, question, correctAnswer, incorrectList))
                }
            }
        } catch (e: Exception) {
            Log.e("TriviaClient", "Error fetching trivia questions", e)
        }
        result
    }
}
