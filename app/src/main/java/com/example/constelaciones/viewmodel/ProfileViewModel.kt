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

    val username = mutableStateOf("")
    val profileImageUrl = mutableStateOf<String?>(null)

    fun loadUserProfile() {
        val user = auth.currentUser ?: return
        database.child("users").child(user.uid).get().addOnSuccessListener { snapshot ->
            username.value = snapshot.child("username").value as? String
                ?: user.email?.substringBefore("@").orEmpty()
            profileImageUrl.value = snapshot.child("profileImageUrl").value as? String
        }
    }

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
            uploadUserData(user.uid, user.email ?: "", username, profileImageUrl.value, onSuccess, onError)
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
        database.child("users").child(uid).setValue(userMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }
}
