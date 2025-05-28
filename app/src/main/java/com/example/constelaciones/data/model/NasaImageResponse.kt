package com.example.constelaciones.data.model

data class NasaImageResponse(
    val collection: Collection
)

data class NasaLibraryResponse(
    val collection: Collection
)

data class Collection(
    val items: List<Item>
)

data class Item(
    val data: List<Data>,
    val links: List<Link>
)

data class Data(
    val title: String,
    val date_created: String,
    val description: String
)

data class Link(
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



