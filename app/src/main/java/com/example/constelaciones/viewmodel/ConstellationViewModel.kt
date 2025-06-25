package com.example.constelaciones.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.constelaciones.data.model.MemoryModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ConstellationViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _memories = MutableStateFlow<List<MemoryModel>>(emptyList())
    val memories: StateFlow<List<MemoryModel>> = _memories

    fun loadMemories() {
        val user = auth.currentUser ?: return

        firestore.collection("memorias")
            .whereEqualTo("idUsuario", user.uid)
            .get()
            .addOnSuccessListener { result ->
                val loadedMemories = mutableListOf<MemoryModel>()

                for (document in result) {
                    val memoryModel = MemoryModel(
                        id = document.getString("id") ?: "",
                        titulo = document.getString("titulo") ?: "",
                        fecha = document.getString("fecha") ?: "",
                        ubicacion = document.getString("ubicacion") ?: "",
                        descripcion = document.getString("descripcion") ?: "",
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                    loadedMemories.add(memoryModel)
                }

                viewModelScope.launch {
                    _memories.value = loadedMemories.map { memory ->
                        if (memory.imageUrl.isBlank()) {
                            val nasaUrl = fetchNasaImage(memory.fecha)
                            memory.copy(imageUrl = nasaUrl)
                        } else memory
                    }
                }
            }
    }

    private suspend fun fetchNasaImage(date: String): String = withContext(Dispatchers.IO) {
        try {
            val apiKey = "tCUSGkgLZCptWme1rPDuoTUU0JQyV3hLRn6uoBlY"
            val url = URL("https://api.nasa.gov/planetary/apod?date=$date&api_key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)

            val mediaType = json.optString("media_type", "image")
            val resultUrl = if (mediaType == "image") {
                json.optString("url", "")
            } else {
                "https://apod.nasa.gov/apod/image/2401/NGC1333_VLT_960.jpg"
            }

            Log.d("ConstellationViewModel", "NASA image for $date: $resultUrl")
            resultUrl
        } catch (e: Exception) {
            Log.e("ConstellationViewModel", "Error fetching NASA image", e)
            "https://apod.nasa.gov/apod/image/2401/NGC1333_VLT_960.jpg"
        }
    }
}
