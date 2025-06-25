package com.example.constelaciones.ui.screens.nuevoRecuerdo

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.constelaciones.ui.components.ScaffoldWithBackground
import com.example.constelaciones.viewmodel.AddMemoryViewModel
import java.util.*

@Composable
fun AddMemoryScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: AddMemoryViewModel = viewModel()

    // Estados de los campos
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Para abrir galería
    val imageLauncher = rememberLauncherForActivityResult(GetContent()) {
        imageUri = it
    }

    // Scroll interno
    val scrollState = rememberScrollState()

    ScaffoldWithBackground(navController = navController) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),   // Margen general
            contentAlignment = Alignment.Center
        ) {
            // Fondo redondeado
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E1B4B), Color(0xFF2E1065))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título
                    Text(
                        text = "Nuevo Recuerdo",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )

                    Spacer(Modifier.height(16.dp))

                    // Selector de imagen
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { imageLauncher.launch("image/*") }
                            .border(2.dp, Color(0xFF7C3AED), RoundedCornerShape(20.dp))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Box {
                                Image(
                                    painter = rememberAsyncImagePainter(imageUri),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar imagen",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(24.dp)
                                        .background(
                                            color = Color.Black.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(4.dp)
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = "Seleccionar imagen",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // --- Aquí empieza la zona scrollable ---
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        StyledTextField(title, { title = it }, "Título del recuerdo")
                        Spacer(Modifier.height(12.dp))
                        DatePickerField(selectedDate) { selectedDate = it }
                        Spacer(Modifier.height(12.dp))
                        StyledTextField(location, { location = it }, "Ubicación (opcional)")
                        Spacer(Modifier.height(12.dp))
                        StyledTextField(
                            value = description.text,
                            onValueChange = { description = TextFieldValue(it) },
                            label = "Descripción (opcional)",
                            singleLine = false
                        )
                    }
                    // --- Fin de la zona scrollable ---

                    Spacer(Modifier.height(16.dp))

                    // Botón fijo al pie del fondo
                    Button(
                        onClick = {
                            if (title.isBlank() || selectedDate.isBlank()) {
                                Toast.makeText(context, "Faltan campos obligatorios", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.saveMemory(
                                    title = title,
                                    date = selectedDate,
                                    location = location,
                                    description = description.text,
                                    imageUri = imageUri,
                                    onSuccess = {
                                        Toast.makeText(context, "Recuerdo guardado", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    },
                                    onError = {
                                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                    ) {
                        Text("Confirmar", color = Color.White)
                    }
                }
            }
        }
    }
}

// Tus helpers quedan tal cual
@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.LightGray
        )
    )
}

@Composable
fun DatePickerField(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text("Fecha del recuerdo", color = Color.White) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = {
                DatePickerDialog(
                    context,
                    { _: DatePicker, year: Int, month: Int, day: Int ->
                        val formatted = "%02d/%02d/%d".format(day, month + 1, year)
                        onDateSelected(formatted)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha", tint = Color.White)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.LightGray
        )
    )
}
