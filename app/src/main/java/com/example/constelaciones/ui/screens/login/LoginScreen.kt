package com.example.constelaciones.ui.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.constelaciones.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter


@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    val user by authViewModel.user.collectAsState()



    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }


    // configuro Google incio sesion
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            authViewModel.firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val signInRequest = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("88400132328-ok5r1878noh37hcttabmidfth55copri.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso).signInIntent
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF16185C), Color(0xFF00021F))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                // Logo centrado
                Image(
                    painter = rememberAsyncImagePainter("https://kksqeezvcyujrkjnkrjo.supabase.co/storage/v1/object/public/logo//ChatGPT%20Image%2025%20jun%202025,%2003_18_21%20a.m..png"),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(180.dp)
                        .padding(bottom = 48.dp),
                    contentScale = ContentScale.Fit
                )

                // Botón Google
                Button(
                    onClick = {
                        launcher.launch(signInRequest)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Iniciar con Google", color = Color.Black)
                }
            }

        }
    }
}
