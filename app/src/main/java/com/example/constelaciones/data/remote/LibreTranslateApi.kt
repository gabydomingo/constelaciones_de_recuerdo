package com.example.constelaciones.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class TranslateRequest(
    val q: String,
    val source: String = "en",
    val target: String = "es",
    val format: String = "text"
)

// LibreTranslate devuelve la respuesta con este nombre de campo exacto
data class TranslateResponse(
    @SerializedName("translatedText")
    val translatedText: String
)

interface LibreTranslateApi {
    @Headers("Content-Type: application/json")
    @POST("translate")
    suspend fun translate(@Body body: TranslateRequest): TranslateResponse
}