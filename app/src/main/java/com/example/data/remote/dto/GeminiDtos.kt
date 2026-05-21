package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "responseFormat") val responseFormat: ResponseFormat? = null,
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "imageConfig") val imageConfig: ImageConfig? = null,
    @Json(name = "responseModalities") val responseModalities: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class ResponseFormat(
    @Json(name = "responseMimeType") val responseMimeType: String
)

@JsonClass(generateAdapter = true)
data class ImageConfig(
    @Json(name = "aspectRatio") val aspectRatio: String, // e.g. "1:1", "16:9"
    @Json(name = "imageSize") val imageSize: String? = null // e.g. "1K", "STANDARD"
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content?
)

// --- Imagen 3 DTOs ---

@JsonClass(generateAdapter = true)
data class ImagenGenerateRequest(
    @Json(name = "instances") val instances: List<ImagenInstance>,
    @Json(name = "parameters") val parameters: ImagenParameters? = null
)

@JsonClass(generateAdapter = true)
data class ImagenInstance(
    @Json(name = "prompt") val prompt: String
)

@JsonClass(generateAdapter = true)
data class ImagenParameters(
    @Json(name = "sampleCount") val sampleCount: Int = 1,
    @Json(name = "aspectRatio") val aspectRatio: String? = null,
    @Json(name = "negativePrompt") val negativePrompt: String? = null
)

@JsonClass(generateAdapter = true)
data class ImagenGenerateResponse(
    @Json(name = "predictions") val predictions: List<ImagenPrediction>?
)

@JsonClass(generateAdapter = true)
data class ImagenPrediction(
    @Json(name = "bytesBase64Encoded") val bytesBase64Encoded: String? = null,
    @Json(name = "mimeType") val mimeType: String? = null
)
