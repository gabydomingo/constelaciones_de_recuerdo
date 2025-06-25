package com.example.constelaciones.data.model

data class NasaImageResponse(
    val collection: NasaCollection
)

data class NasaLibraryResponse(
    val collection: NasaCollection
)

data class NasaCollection(
    val items: List<NasaItem>
)

data class NasaItem(
    val data: List<NasaData>,
    val links: List<NasaLink>
)

data class NasaData(
    val title: String,
    val date_created: String,
    val description: String,
    val media_type: String // ✅ Este es el campo que te faltaba
)

data class NasaLink(
    val href: String
)

data class ApodResponse(
    val date: String,
    val explanation: String,
    val hdurl: String?,
    val media_type: String,
    val service_version: String,
    val title: String,
    val url: String
)

data class TranslateResponse(
    val translatedText: String
)

data class NasaEvent(
    val title: String,
    val date: String,
    val description: String,
    val imageUrl: String
)
