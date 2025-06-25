package com.example.constelaciones.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

object NasaRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://images-api.nasa.gov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(NasaApiService::class.java)

    private val keywords = listOf("galaxy", "star", "nebula", "space", "supernova")

    suspend fun fetchNasaImage(anyDate: String? = null): String? {
        return withContext(Dispatchers.IO) {
            try {
                val randomKeyword = keywords.random()
                val response = api.searchImages(randomKeyword)

                val items = response.collection.items
                    .filter { it.links.isNotEmpty() && it.data.firstOrNull()?.title != null }

                if (items.isNotEmpty()) {
                    val randomItem = items.random()
                    return@withContext randomItem.links.firstOrNull()?.href
                } else {
                    Log.e("NASA_IMAGE", "No image items found for keyword $randomKeyword")
                    null
                }
            } catch (e: Exception) {
                Log.e("NASA_IMAGE", "Error fetching image: ${e.message}")
                null
            }
        }
    }
}
