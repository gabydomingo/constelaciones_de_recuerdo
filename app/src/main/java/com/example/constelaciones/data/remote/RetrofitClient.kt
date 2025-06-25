package com.example.constelaciones.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "https://api.nasa.gov/"
    private const val BASE_SEARCH_URL = "https://images-api.nasa.gov/"
    private const val API_KEY = "tCUSGkgLZCptWme1rPDuoTUU0JQyV3hLRn6uoBLY"


    // Cliente para APOD

    val nasaApi: NasaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaApiService::class.java)
    }

    // Cliente para Search (para usar separadoss)

    val nasaSearchApi: NasaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_SEARCH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaApiService::class.java)
    }

    val nasaEventApi: NasaEventApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://images-api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaEventApi::class.java)
    }



}





