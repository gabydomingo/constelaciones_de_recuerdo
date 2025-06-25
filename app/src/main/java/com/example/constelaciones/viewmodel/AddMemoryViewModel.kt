package com.example.constelaciones.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddMemoryViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    fun saveMemory(
        title: String,
        date: String,
        location: String,
        description: String,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val user = auth.currentUser ?: return
        val memoryId = UUID.randomUUID().toString()
        val creationDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        fun uploadData(imageUrl: String?) {
            val memory = mapOf(
                "id" to memoryId,
                "titulo" to title,
                "fecha" to date,
                "ubicacion" to location,
                "descripcion" to description,
                "imageUrl" to (imageUrl ?: ""),
                "idUsuario" to user.uid,
                "fechaCreacion" to creationDate
            )

            database.collection("memorias").document(memoryId)
                .set(memory)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        }

        if (imageUri != null) {
            val imageRef = storage.child("memories/${user.uid}/$memoryId.jpg")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        uploadData(uri.toString())
                    }.addOnFailureListener(onError)
                }
                .addOnFailureListener(onError)
        } else {
            uploadData(null)
        }
    }
}
