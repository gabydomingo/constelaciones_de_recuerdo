
package com.example.constelaciones.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LibreTranslateClient {
    // siempre tengo que poner esta url con / al final
    private const val BASE_URL = "https://libretranslate.com/api/v1/"

    val api: LibreTranslateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LibreTranslateApi::class.java)
    }
}