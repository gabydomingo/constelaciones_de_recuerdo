package com.example.constelaciones.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object MyMemoryTranslateClient {
    private const val BASE_URL = "https://api.mymemory.translated.net/"

    val api: MyMemoryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyMemoryApi::class.java)
    }
}

data class MyMemoryResponse(
    @SerializedName("responseData")
    val responseData: ResponseData,
    @SerializedName("responseStatus")
    val responseStatus: Int
)

data class ResponseData(
    @SerializedName("translatedText")
    val translatedText: String
)

interface MyMemoryApi {
    @GET("get")
    suspend fun translate(
        @Query("q") text: String,
        @Query("langpair") langPair: String = "en|es",
        @Query("de") email: String = "domingogaby8@gmail.com",
        @Query("key") apiKey: String = "4baef49ce12c2609e0a2"
    ): MyMemoryResponse
}