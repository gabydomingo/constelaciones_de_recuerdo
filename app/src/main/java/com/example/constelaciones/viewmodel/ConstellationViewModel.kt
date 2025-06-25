package com.example.constelaciones.viewmodel

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.constelaciones.data.model.MemoryModel
import com.example.constelaciones.data.remote.NasaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class ConstellationViewModel : ViewModel() {

    private val _memories = MutableStateFlow<List<MemoryModel>>(emptyList())
    val memories = _memories.asStateFlow()

    // Mapa id → posición (Offset.x, Offset.y), valores normalizados en [0f,1f)
    private val _positions = MutableStateFlow<Map<String, Offset>>(emptyMap())
    val positions = _positions.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun loadMemories() {
        if (userId == null) return

        firestore.collection("memorias")
            .whereEqualTo("idUsuario", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                viewModelScope.launch {
                    val memoryList = mutableListOf<MemoryModel>()
                    val posMap = _positions.value.toMutableMap()

                    snapshot.documents.forEach { doc ->
                        val memory = doc.toObject(MemoryModel::class.java) ?: return@forEach
                        memory.id = doc.id
                        memory.isFavorito = doc.getBoolean("isFavorito") ?: false

                        // Generar posición determinística si aún no existe
                        if (!posMap.containsKey(doc.id)) {
                            val seedX = doc.id.hashCode().toLong()
                            val seedY = doc.id.hashCode().inv().toLong()
                            posMap[doc.id] = Offset(
                                x = Random(seedX).nextFloat(),
                                y = Random(seedY).nextFloat()
                            )
                        }

                        // Si falta imagen, obtenerla de la API de la NASA
                        if (memory.imageUrl.isBlank()) {
                            val fetchedUrl = NasaRepository.fetchNasaImage(memory.fecha)
                            if (!fetchedUrl.isNullOrBlank()) {
                                firestore.collection("memorias")
                                    .document(doc.id)
                                    .update("imageUrl", fetchedUrl)
                                memory.imageUrl = fetchedUrl
                            }
                        }

                        memoryList += memory
                    }

                    _positions.value = posMap
                    _memories.value = memoryList

                    Log.d("VIEWMODEL", "Total recuerdos: ${memoryList.size}")
                    Log.d("VIEWMODEL", "Favoritos: ${memoryList.count { it.isFavorito }}")
                }
            }
    }

    fun toggleFavorito(memory: MemoryModel) {
        val nuevoValor = !memory.isFavorito
        memory.isFavorito = nuevoValor

        firestore.collection("memorias")
            .document(memory.id)
            .update("isFavorito", nuevoValor)

        _memories.value = _memories.value.map {
            if (it.id == memory.id) memory else it
        }
    }

    /**
     * Actualiza los campos de un recuerdo, opcionalmente subiendo
     * una nueva imagen si newImageUri != null.
     */
    fun updateMemory(
        memoryId: String,
        newTitle: String,
        newDate: String,
        newLocation: String,
        newDescription: String,
        newImageUri: android.net.Uri?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val docRef = firestore.collection("memorias").document(memoryId)

        if (newImageUri != null) {
            // Subir nueva imagen
            val imgRef = storage.child("memorias_images/$memoryId.jpg")
            imgRef.putFile(newImageUri)
                .addOnSuccessListener {
                    imgRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            persistMemory(
                                docRef,
                                newTitle, newDate, newLocation, newDescription,
                                uri.toString(),
                                onSuccess, onError
                            )
                        }
                        .addOnFailureListener(onError)
                }
                .addOnFailureListener(onError)
        } else {
            // Mantener la URL de imagen existente
            val existingUrl = _memories.value
                .firstOrNull { it.id == memoryId }
                ?.imageUrl
                .orEmpty()

            persistMemory(
                docRef,
                newTitle, newDate, newLocation, newDescription,
                existingUrl,
                onSuccess, onError
            )
        }
    }

    private fun persistMemory(
        docRef: DocumentReference,
        title: String,
        date: String,
        location: String,
        description: String,
        imageUrl: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val updates = mapOf(
            "titulo" to title,
            "fecha" to date,
            "ubicacion" to location,
            "descripcion" to description,
            "imageUrl" to imageUrl
        )
        docRef.update(updates)
            .addOnSuccessListener {
                // Refrescar localmente el flujo de recuerdos
                _memories.value = _memories.value.map {
                    if (it.id == docRef.id) it.copy(
                        titulo      = title,
                        fecha       = date,
                        ubicacion   = location,
                        descripcion = description,
                        imageUrl    = imageUrl
                    ) else it
                }
                onSuccess()
            }
            .addOnFailureListener(onError)
    }
}
