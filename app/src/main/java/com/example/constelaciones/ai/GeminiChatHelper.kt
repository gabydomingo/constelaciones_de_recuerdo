package com.example.constelaciones.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class GeminiChatHelper(private val apiKey: String) {

    //modelo y api key
    private val generativeModel = GenerativeModel(
        modelName = "models/gemini-2.0-flash",
        apiKey = "AIzaSyCWeuwSG7GfNoNZDeRsFfOfm2x3AhpVFm0"
    )

    suspend fun chatWithGemini(userMessage: String): String {
        return try {
            val inputContent = content { text(userMessage) }
            val response = generativeModel.generateContent(inputContent)
            response.text ?: "No hubo respuesta de Gemini."
        } catch (e: com.google.ai.client.generativeai.type.GoogleGenerativeAIException) {
            "Error específico de Gemini API: ${e.message} (Causa: ${e.cause?.message})"
        } catch (e: kotlinx.serialization.MissingFieldException) {
            "Error de deserialización: ${e.message}. La respuesta de error de la API no tuvo el formato esperado."
        } catch (e: Exception) {
            "Error al generar respuesta: ${e.localizedMessage}"
        }
    }
}
