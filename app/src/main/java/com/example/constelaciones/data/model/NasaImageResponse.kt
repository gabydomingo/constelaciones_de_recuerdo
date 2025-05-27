package com.example.constelaciones.data.model

data class NasaImageResponse(
    val collection: Collection
)

data class Collection(
    val items: List<NasaItem>
)

data class NasaItem(
    val data: List<ImageData>,
    val links: List<ImageLink>
)

data class ImageData(
    val title: String,
    val description: String
)

data class ImageLink(
    val href: String
)
