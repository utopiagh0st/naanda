package com.example.hubdejuegos.ui.screens

import android.text.Html
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hubdejuegos.data.api.TriviaRepository
import com.example.hubdejuegos.data.api.TriviaDto
import kotlinx.coroutines.launch

@Composable
fun TriviaScreen(
    onCorrectAnswer: (Int) -> Unit
) {
    val repository = remember { TriviaRepository() }
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<TriviaDto>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var score by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            val fetched = repository.getTriviaQuestions()
            questions = fetched
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🧠 Trivia de Videojuegos", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("¡Pon a prueba tus conocimientos y gana puntos!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (questions.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("No se pudieron cargar las preguntas. Verifica tu conexión.")
            }
        } else if (currentIndex >= questions.size) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉 ¡Trivia Completada!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Aciertos: $score / ${questions.size}", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        scope.launch {
                            isLoading = true
                            currentIndex = 0
                            score = 0
                            questions = repository.getTriviaQuestions()
                            isLoading = false
                        }
                    }) {
                        Text("Jugar Nuevamente")
                    }
                }
            }
        } else {
            val currentQuestion = questions[currentIndex]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pregunta ${currentIndex + 1} de ${questions.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    val decodedQuestion = Html.fromHtml(currentQuestion.question, Html.FROM_HTML_MODE_LEGACY).toString()
                    Text(decodedQuestion, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val options = remember(currentQuestion) { currentQuestion.allOptions }
            options.forEach { option ->
                val decodedOption = Html.fromHtml(option, Html.FROM_HTML_MODE_LEGACY).toString()
                val isSelected = selectedAnswer == option
                val buttonColor = when {
                    selectedAnswer == null -> ButtonDefaults.buttonColors()
                    option == currentQuestion.correctAnswer -> ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    isSelected -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    else -> ButtonDefaults.buttonColors()
                }

                Button(
                    onClick = {
                        if (selectedAnswer == null) {
                            selectedAnswer = option
                            val correct = option == currentQuestion.correctAnswer
                            isCorrect = correct
                            if (correct) {
                                score++
                                onCorrectAnswer(20)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = buttonColor
                ) {
                    Text(decodedOption, fontSize = 14.sp)
                }
            }

            if (selectedAnswer != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedAnswer = null
                        isCorrect = null
                        currentIndex++
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Siguiente Pregunta ➡️")
                }
            }
        }
    }
}
