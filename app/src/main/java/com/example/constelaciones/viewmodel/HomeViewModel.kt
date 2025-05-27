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

class HomeViewModel : ViewModel() {

    private val _apod = MutableStateFlow<ApodResponse?>(null)
    val apod = _apod.asStateFlow()

    private val _imageTitle = MutableStateFlow<String?>(null)
    val imageTitle = _imageTitle.asStateFlow()

    private var hasFetched = false  // Para evitar llamadas múltiples

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
                    _imageTitle.value = response.title
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
}
