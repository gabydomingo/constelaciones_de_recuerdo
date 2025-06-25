package com.example.constelaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.constelaciones.data.model.MemoryModel
import com.example.constelaciones.data.remote.NasaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConstellationViewModel : ViewModel() {

    private val _memories = MutableStateFlow<List<MemoryModel>>(emptyList())
    val memories = _memories.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun loadMemories() {
        if (userId == null) return

        firestore.collection("memorias")
            .whereEqualTo("idUsuario", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val memoryList = mutableListOf<MemoryModel>()

                snapshot.documents.forEach { doc ->
                    val memory = doc.toObject(MemoryModel::class.java)
                    if (memory != null) {
                        val idDoc = doc.id

                        // Verifica si falta imageUrl
                        if (memory.imageUrl.isNullOrBlank()) {
                            viewModelScope.launch {
                                val fetchedUrl = NasaRepository.fetchNasaImage(memory.fecha)
                                if (fetchedUrl != null) {
                                    firestore.collection("memorias")
                                        .document(idDoc)
                                        .update("imageUrl", fetchedUrl)
                                    memory.imageUrl = fetchedUrl
                                }
                                memoryList.add(memory)
                                _memories.value = memoryList.toList()
                            }
                        } else {
                            memoryList.add(memory)
                            _memories.value = memoryList.toList()
                        }
                    }
                }
            }
    }

    fun toggleFavorito(memory: MemoryModel) {
        val nuevoValor = !memory.isFavorito
        memory.isFavorito = nuevoValor

        viewModelScope.launch {
            firestore.collection("memorias")
                .whereEqualTo("idUsuario", userId)
                .whereEqualTo("titulo", memory.titulo)
                .whereEqualTo("fecha", memory.fecha)
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.documents.firstOrNull()?.id?.let { docId ->
                        firestore.collection("memorias")
                            .document(docId)
                            .update("isFavorito", nuevoValor)
                    }
                }
        }

        // Actualizamos la lista local
        val nuevaLista = _memories.value.map {
            if (it.fecha == memory.fecha && it.titulo == memory.titulo) memory else it
        }
        _memories.value = nuevaLista
    }
}
