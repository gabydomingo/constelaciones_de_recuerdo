package com.example.constelaciones.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference

    // Estados observables
    val username = mutableStateOf("")
    val profileImageUrl = mutableStateOf<String?>(null)

    /** Lee username y profileImageUrl desde Realtime Database */
    fun loadUserProfile() {
        val user = auth.currentUser ?: return
        database.child("users").child(user.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                // Si no hay username, usamos la parte del email antes de la @
                username.value = snapshot.child("username").getValue(String::class.java)
                    ?: user.email?.substringBefore("@").orEmpty()
                profileImageUrl.value = snapshot.child("profileImageUrl").getValue(String::class.java)
            }
    }

    /**
     * Sube la nueva imagen (si la hay) y luego guarda username e imageUrl en Realtime Database
     */
    fun saveUserProfile(
        username: String,
        profileImageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val user = auth.currentUser ?: return

        if (profileImageUri != null) {
            val fileRef = storage.child("profile_images/${user.uid}.jpg")
            fileRef.putFile(profileImageUri)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        uploadUserData(user.uid, user.email ?: "", username, uri.toString(), onSuccess, onError)
                    }
                }
                .addOnFailureListener(onError)
        } else {
            uploadUserData(
                uid = user.uid,
                email = user.email ?: "",
                username = username,
                imageUrl = profileImageUrl.value,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    private fun uploadUserData(
        uid: String,
        email: String,
        username: String,
        imageUrl: String?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userMap = mapOf(
            "uid" to uid,
            "email" to email,
            "username" to username,
            "profileImageUrl" to (imageUrl ?: "")
        )
        database.child("users").child(uid)
            .setValue(userMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}
