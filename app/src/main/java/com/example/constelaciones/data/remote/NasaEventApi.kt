package com.example.constelaciones.data.remote

import com.example.constelaciones.data.model.NasaLibraryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaEventApi {
    @GET("search")
    suspend fun searchEvents(
        @Query("q") query: String = "astronomy",
        @Query("media_type") mediaType: String = "image"
    ): NasaLibraryResponse
}
