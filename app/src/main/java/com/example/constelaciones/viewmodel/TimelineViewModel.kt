package com.example.constelaciones.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.constelaciones.data.model.NasaEvent
import com.example.constelaciones.data.remote.LibreTranslateClient
import com.example.constelaciones.data.remote.MyMemoryTranslateClient
import com.example.constelaciones.data.remote.RetrofitClient
import com.example.constelaciones.data.remote.TranslateRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TimelineViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<NasaEvent>>(emptyList())
    val events = _events.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<NasaEvent>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _translatedDescriptions = mutableMapOf<String, String>()

    init {
        fetchAstronomyEvents()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query

        if (query.isNotBlank()) {
            searchEventsFromApi(query)
        } else {
            _searchResults.value = emptyList()
        }
    }

    private fun fetchAstronomyEvents() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.nasaEventApi.searchEvents()
                val items = response.collection.items

                val events = items.mapNotNull { item ->
                    val data = item.data.firstOrNull()
                    val image = item.links.firstOrNull()?.href

                    if (data != null && image != null) {
                        NasaEvent(
                            title = data.title,
                            date = data.date_created.substring(0, 10),
                            description = data.description,
                            imageUrl = image
                        )
                    } else null
                }

                _events.value = events
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun searchEventsFromApi(query: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = RetrofitClient.nasaEventApi.searchEvents(query = query)
                val items = response.collection.items

                val events = items.mapNotNull { item ->
                    val data = item.data.firstOrNull()
                    val image = item.links.firstOrNull()?.href

                    if (data != null && image != null) {
                        NasaEvent(
                            title = data.title,
                            date = data.date_created.substring(0, 10),
                            description = data.description,
                            imageUrl = image
                        )
                    } else null
                }

                _searchResults.value = events
            } catch (e: Exception) {
                e.printStackTrace()
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función de traducción mantenida para posibles usos futuros (ya no usada aquí)
    private suspend fun translateWithFallback(original: String): String {
        _translatedDescriptions[original]?.let { return it }

        try {
            val memoryResponse = MyMemoryTranslateClient.api.translate(
                text = original,
                langPair = "en|es",
                email = "domingogaby8@gmail.com",
                apiKey = "4baef49ce12c2609e0a2"
            )

            if (memoryResponse.responseStatus == 200) {
                val result = memoryResponse.responseData.translatedText
                _translatedDescriptions[original] = result
                return result
            } else {
                Log.e("TRANSLATE", "MyMemory falló, probando fallback...")
            }

        } catch (e: Exception) {
            Log.e("TRANSLATE", "Error en MyMemory: ${e.message}")
        }

        return try {
            val libreResponse = LibreTranslateClient.api.translate(
                TranslateRequest(q = original)
            )
            val result = libreResponse.translatedText
            _translatedDescriptions[original] = result
            result
        } catch (e: Exception) {
            Log.e("TRANSLATE", "Error en LibreTranslate: ${e.message}")
            original
        }
    }
}