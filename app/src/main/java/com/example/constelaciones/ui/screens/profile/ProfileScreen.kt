package com.example.constelaciones.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    val username by viewModel.username
    val profileImageUrl by viewModel.profileImageUrl
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val email = firebaseUser?.email.orEmpty()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    val painter = rememberAsyncImagePainter(
        model = selectedImageUri ?: profileImageUrl.orEmpty()
    )

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    ScaffoldWithBackground(navController = navController) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") }
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (selectedImageUri == null && profileImageUrl.isNullOrBlank()) {
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

                Button(
                    onClick = {
                        viewModel.saveUserProfile(
                            username = username,
                            profileImageUri = selectedImageUri,
                            onSuccess = {
                                selectedImageUri = null
                                scope.launch {
                                    snackbarHostState.showSnackbar("Perfil guardado correctamente")
                                }
                            },
                            onError = { e ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("Error al guardar: ${e.message}")
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Text("Guardar cambios", color = Color.White)
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
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cerrar sesión", color = Color.White)
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}
