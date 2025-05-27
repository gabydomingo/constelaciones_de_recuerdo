package com.example.constelaciones.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddMemoryViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
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

        if (imageUri != null) {
            val imageRef = storage.child("memories/${user.uid}/$memoryId.jpg")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        uploadMemoryData(
                            user.uid,
                            memoryId,
                            title,
                            date,
                            location,
                            description,
                            uri.toString(),
                            onSuccess,
                            onError
                        )
                    }
                }
                .addOnFailureListener(onError)
        } else {
            uploadMemoryData(
                user.uid,
                memoryId,
                title,
                date,
                location,
                description,
                null,
                onSuccess,
                onError
            )
        }
    }

    private fun uploadMemoryData(
        uid: String,
        memoryId: String,
        title: String,
        date: String,
        location: String,
        description: String,
        imageUrl: String?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val memory = mapOf(
            "id" to memoryId,
            "title" to title,
            "date" to date,
            "location" to location,
            "description" to description,
            "imageUrl" to (imageUrl ?: "")
        )

        database.child("memories").child(uid).child(memoryId).setValue(memory)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }
}
