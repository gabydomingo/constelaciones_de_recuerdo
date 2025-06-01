package com.example.constelaciones.ui.screens.profile

///modificar mas adelante: guarda la img en firebase pero no la muestra en app
/// tomar info del mail solo parte ddelante del arroba.

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.constelaciones.ui.components.ScaffoldWithBackground
import com.example.constelaciones.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val email = firebaseUser?.email ?: ""

    val viewModel: ProfileViewModel = viewModel()
    val username by viewModel.username
    val profileImageUrl by viewModel.profileImageUrl

    val snackbarHostState = remember { SnackbarHostState() }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    // cargar datos del usuario al entrar (funciona y no funciona)
    scope.launch {
        snackbarHostState.showSnackbar("Perfil guardado correctamente")
    }

    val launcher = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
        }
    }

    val imagePainter = rememberAsyncImagePainter(
        model = profileImageUri ?: profileImageUrl ?: ""
    )

    ScaffoldWithBackground(navController = navController) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // imagen de perfil (funciona y no)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") }
                ) {
                    Image(
                        painter = imagePainter,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (profileImageUri == null && profileImageUrl.isNullOrEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Icono perfil",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(60.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Correo: $email", color = Color.White)

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = username,
                    onValueChange = { viewModel.username.value = it },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = {
                    viewModel.saveUserProfile(
                        viewModel.username.value,
                        profileImageUri,
                        onSuccess = {
                            profileImageUri = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Perfil guardado correctamente")
                            }
                        },
                        onError = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Error al guardar: ${it.message}")
                            }
                        }
                    )
                }) {
                    Text("Guardar cambios")
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cerrar sesión", color = Color.White)
                }
            }
        }
    }
}
