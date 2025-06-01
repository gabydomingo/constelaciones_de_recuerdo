package com.example.constelaciones.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.constelaciones.data.model.ApodResponse
import com.example.constelaciones.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.constelaciones.data.remote.MyMemoryTranslateClient

class HomeViewModel : ViewModel() {

    private val _apod = MutableStateFlow<ApodResponse?>(null)
    val apod = _apod.asStateFlow()

    private val _imageTitle = MutableStateFlow<String?>(null)
    val imageTitle = _imageTitle.asStateFlow()

    private val _translatedExplanation = MutableStateFlow<String?>(null)
    val translatedExplanation = _translatedExplanation.asStateFlow()

    private var hasFetched = false // Para evitar llamadas múltiples

    init {
        fetchTodayImageIfNeeded()
    }

    fun fetchTodayImageIfNeeded() {
        if (hasFetched || _apod.value != null) {
            Log.d("APOD_INFO", "Imagen ya cargada, no se vuelve a pedir.")
            return
        }

        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val datesToTry = listOf(
                sdf.format(calendar.time),      // hoy
                sdf.format(calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.time) // ayer
            )

            for (date in datesToTry) {
                try {
                    val response = RetrofitClient.nasaApi.getApod(
                        date = date,
                        apiKey = "tCUSGkgLZCptWme1rPDuoTUU0JQyV3hLRn6uoBlY"
                    )
                    Log.d("APOD_URL", "Imagen para $date: ${response.url}")
                    _apod.value = response
                    _imageTitle.value = "Imagen del día"
                    translateExplanation(response.explanation)
                    hasFetched = true
                    return@launch
                } catch (e: retrofit2.HttpException) {
                    if (e.code() == 429) {
                        Log.e("APOD_ERROR", "Límite de peticiones superado (429)")
                        break
                    } else {
                        Log.w("APOD_RETRY", "No disponible para $date: ${e.message()}")
                    }
                } catch (e: Exception) {
                    Log.w("APOD_RETRY", "Error general al intentar $date: ${e.message}")
                }
            }

            Log.e("APOD_ERROR", "No se pudo obtener imagen ni para hoy ni para ayer")
        }
    }

    private fun translateExplanation(originalText: String) {
        viewModelScope.launch {
            try {
                Log.d("TRANSLATE_INFO", "Iniciando traducción con MyMemory...")
                Log.d("TRANSLATE_TEXT", "Texto a traducir (primeros 100 chars): ${originalText.take(100)}...")


                val textToTranslate = originalText


                val response = MyMemoryTranslateClient.api.translate(
                    text = textToTranslate,
                    langPair = "en|es",
                    email = "constelaciones@example.com"
                )

                Log.d("TRANSLATE_RESPONSE", "Status: ${response.responseStatus}")

                if (response.responseStatus == 200) {
                    _translatedExplanation.value = response.responseData.translatedText
                    Log.d("TRANSLATE_SUCCESS", "Traducción exitosa: ${response.responseData.translatedText.take(50)}...")
                } else {
                    Log.e("TRANSLATE_ERROR", "Error en respuesta: ${response.responseStatus}")
                    _translatedExplanation.value = originalText
                }

            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("TRANSLATE_ERROR", "Error HTTP: ${e.code()} - ${e.message()}")
                Log.e("TRANSLATE_ERROR", "Error body: $errorBody")
                _translatedExplanation.value = originalText
            } catch (e: Exception) {
                Log.e("TRANSLATE_ERROR", "Error general al traducir: ${e.message}", e)
                _translatedExplanation.value = originalText
            }


        }

    }

    private val _isSpanish = MutableStateFlow(true)
    val isSpanish = _isSpanish.asStateFlow()

    fun toggleLanguage() {
        _isSpanish.value = !_isSpanish.value
    }


}
