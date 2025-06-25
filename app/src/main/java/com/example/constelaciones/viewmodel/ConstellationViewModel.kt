package com.example.constelaciones.viewmodel

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.constelaciones.data.model.MemoryModel
import com.example.constelaciones.data.remote.NasaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class ConstellationViewModel : ViewModel() {

    private val _memories = MutableStateFlow<List<MemoryModel>>(emptyList())
    val memories = _memories.asStateFlow()

    // Aquí guardamos las posiciones, clave = memory.id
    private val _positions = MutableStateFlow<Map<String,Offset>>(emptyMap())
    val positions = _positions.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
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
                        // Leemos el campo real
                        memory.isFavorito = doc.getBoolean("isFavorito") ?: false

                        // Generamos posición si aún no existe
                        if (!posMap.containsKey(doc.id)) {
                            posMap[doc.id] = Offset(
                                x = Random(doc.id.hashCode()).nextFloat(),
                                y = Random(doc.id.hashCode().inv()).nextFloat()
                            )
                        }

                        // Fetch de imagen si falta
                        if (memory.imageUrl.isBlank()) {
                            val fetchedUrl = NasaRepository.fetchNasaImage(memory.fecha)
                            if (!fetchedUrl.isNullOrBlank()) {
                                firestore.collection("memorias")
                                    .document(doc.id)
                                    .update("imageUrl", fetchedUrl)
                                memory.imageUrl = fetchedUrl
                            }
                        }

                        // guardamos el id dentro del modelo (útil para lookup)
                        memory.id = doc.id
                        memoryList += memory
                    }

                    // Actualizamos ambos flujos de estado de una vez
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

        viewModelScope.launch {
            firestore.collection("memorias")
                .document(memory.id)
                .update("isFavorito", nuevoValor)
        }

        // Actualiza tu lista local
        _memories.value = _memories.value.map {
            if (it.id == memory.id) memory else it
        }
    }
}
