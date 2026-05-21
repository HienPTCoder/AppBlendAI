package com.example.data.remote

import com.example.data.remote.dto.GenerateContentRequest
import com.example.data.remote.dto.GenerateContentResponse
import com.example.data.remote.dto.ImagenGenerateRequest
import com.example.data.remote.dto.ImagenGenerateResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse

    @POST("v1beta/models/{model}:predict")
    suspend fun predict(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: ImagenGenerateRequest
    ): ImagenGenerateResponse
}
