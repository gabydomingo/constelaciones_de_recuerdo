package com.example.constelaciones.ui.screens.chatScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val scope = rememberCoroutineScope()
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    val conversation = remember { mutableStateListOf<Pair<String, String>>() }

    val generativeModel = remember {
        GenerativeModel(
            modelName = "models/gemini-2.0-flash",
            apiKey = "AIzaSyCWeuwSG7GfNoNZDeRsFfOfm2x3AhpVFm0"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistente IA", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF16185C)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF16185C), Color(0xFF00021F))
                    )
                )
                .padding(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                reverseLayout = true
            ) {
                items(conversation.reversed()) { (user, bot) ->
                    // Usuario
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF4F8EF7), shape = RoundedCornerShape(16.dp))
                                .padding(12.dp)
                                .widthIn(max = 280.dp)
                        ) {
                            Text(user, color = Color.White, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Gemini
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2C2C40), shape = RoundedCornerShape(16.dp))
                                .padding(12.dp)
                                .widthIn(max = 280.dp)
                        ) {
                            Text(bot, color = Color.LightGray, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text("Pregúntame lo que quieras...") },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White.copy(alpha = 0.05f), shape = MaterialTheme.shapes.small),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                IconButton(
                    onClick = {
                        val question = userInput.text.trim()
                        if (question.isNotBlank()) {
                            conversation.add(question to "...")
                            userInput = TextFieldValue("")


                            //prompt para darle contexto  a la IA y manejarla.
                            scope.launch {
                                val response = try {
                                    withContext(Dispatchers.IO) {
                                        generativeModel.generateContent(
                                            content {
                                                text(
                                                    """
                                                    Eres un experto en astronomía que trabaja para la NASA. 
                                                    Responde preguntas sobre constelaciones, planetas, el espacio, galaxias, fenómenos astronómicos, etc.
                                                    Sé claro, educativo, profesional y amigable.
                                                    """.trimIndent()
                                                )
                                                text(question)
                                            }
                                        ).text
                                    } ?: "No se pudo obtener respuesta."
                                } catch (e: Exception) {
                                    Log.e("GeminiChat", "Error: ${e.message}")
                                    "Ocurrió un error al conectar con la IA."
                                }

                                val index = conversation.indexOfLast { it.first == question }
                                if (index != -1) {
                                    conversation[index] = question to response
                                }
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
                }
            }
        }
    }
}
