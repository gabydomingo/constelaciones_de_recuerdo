package com.example.constelaciones.data.model

import com.google.firebase.database.PropertyName

data class MemoryModel(
    var id: String = "",
    val idUsuario: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var fecha: String = "",
    var ubicacion: String = "",
    var imageUrl: String = "",
    var fechaCreacion: String = "",
    var isFavorito: Boolean = false
)