package com.example.constelaciones.data.remote

import com.example.constelaciones.data.model.ApodResponse
import com.example.constelaciones.data.model.NasaImageResponse
import retrofit2.http.GET
import retrofit2.http.Query

////////////

interface NasaApiService {
    @GET("search")
    suspend fun searchImages(
        @Query("q") query: String = "galaxy"
    ): NasaImageResponse

    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String = "tCUSGkgLZCptWme1rPDuoTUU0JQyV3hLRn6uoBlY",
        @Query("date") date: String? = null
    ): ApodResponse

}
