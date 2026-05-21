package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.example.data.local.ArtworkDao
import com.example.data.local.ArtworkEntity
import com.example.data.remote.GeminiApiService
import com.example.data.remote.dto.Content
import com.example.data.remote.dto.GenerateContentRequest
import com.example.data.remote.dto.GenerationConfig
import com.example.data.remote.dto.ImagenGenerateRequest
import com.example.data.remote.dto.ImagenInstance
import com.example.data.remote.dto.ImagenParameters
import com.example.data.remote.dto.InlineData
import com.example.data.remote.dto.Part
import com.example.domain.model.AspectRatio
import com.example.domain.model.GeneratedArtwork
import com.example.domain.model.ImageQuality
import com.example.domain.model.ImageReferenceMode
import com.example.domain.model.ImageStyle
import com.example.domain.repository.ArtworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class ArtworkRepositoryImpl(
    private val context: Context,
    private val artworkDao: ArtworkDao,
    private val apiService: GeminiApiService,
    private val defaultApiKey: String
) : ArtworkRepository {

    override fun getArtworksFlow(): Flow<List<GeneratedArtwork>> {
        return artworkDao.getAllArtworks().map { list -> list.map { it.toDomain() } }
    }

    override fun getFavoriteArtworksFlow(): Flow<List<GeneratedArtwork>> {
        return artworkDao.getFavoriteArtworks().map { list -> list.map { it.toDomain() } }
    }

    override fun getDownloadedArtworksFlow(): Flow<List<GeneratedArtwork>> {
        return artworkDao.getDownloadedArtworks().map { list -> list.map { it.toDomain() } }
    }

    override fun searchArtworksFlow(query: String): Flow<List<GeneratedArtwork>> {
        val formattedQuery = "%$query%"
        return artworkDao.searchArtworks(formattedQuery).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getArtworkById(id: Long): GeneratedArtwork? {
        return artworkDao.getArtworkById(id)?.toDomain()
    }

    override suspend fun saveArtwork(artwork: GeneratedArtwork): Long {
        return artworkDao.insertArtwork(ArtworkEntity.fromDomain(artwork))
    }

    override suspend fun deleteArtwork(id: Long) {
        artworkDao.deleteArtworkById(id)
    }

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        artworkDao.updateFavorite(id, isFavorite)
    }

    override suspend fun markAsDownloaded(id: Long, isDownloaded: Boolean) {
        artworkDao.updateDownloaded(id, isDownloaded)
    }

    override suspend fun generateArtwork(
        prompt: String,
        negativePrompt: String?,
        style: ImageStyle,
        aspectRatio: AspectRatio,
        quality: ImageQuality,
        overrideApiKey: String?,
        referenceImageUri: Uri?,
        referenceMode: ImageReferenceMode
    ): GeneratedArtwork = withContext(Dispatchers.IO) {
        val apiKey = if (!overrideApiKey.isNullOrBlank()) overrideApiKey else defaultApiKey
        
        var parsedPrompt = prompt
        
        // 1. If we have a valid API Key, attempt to translate/enhance the prompt to descriptive English first!
        // This is highly recommended because the Gemini image model and fallback services perform exponentially better on detailed English prompts.
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val translationPrompt = """
                    You are an expert AI image prompt translator and optimizer.
                    Your task is to translate the user's input prompt (which may be in Vietnamese, English, or any other language) into a highly descriptive, detailed English prompt suitable for generating incredible AI artwork.
                    Ensure that:
                    1. The core meaning, dynamic subjects, actions, and key colors are fully preserved.
                    2. It is expanded slightly with rich visual details appropriate for the scene (textures, lighting, atmosphere).
                    3. Do not include style quality buzzwords (like 'realistic', '4k', 'ultra-detailed') since styles are handled separately.
                    
                    Output ONLY the final expanded/translated content in English, without any surrounding quotation marks, preamble, introduction, or explanations.
                    
                    Input: "$prompt"
                """.trimIndent()

                val translationRequest = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = translationPrompt)))
                    ),
                    generationConfig = GenerationConfig(
                        temperature = 0.3f
                    )
                )

                val translationResponse = apiService.generateContent(
                    model = "gemini-2.0-flash",
                    apiKey = apiKey,
                    request = translationRequest
                )

                val translatedText = translationResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!translatedText.isNullOrBlank()) {
                    parsedPrompt = translatedText.trim().removeSurrounding("\"").removeSurrounding("'")
                }
            } catch (e: Exception) {
                // Fail-safe, stay with original prompt
                e.printStackTrace()
            }
        }

        // 2. Construct final AI text prompt enhancing user input with selected style attributes
        // Negative prompt is passed separately to Imagen 3, not embedded here
        val enhancedPrompt = "$parsedPrompt. Style: ${style.promptEnhancement}"

        var savedFilePath: String? = null
        var isRealGemini = false

        // Read reference image bytes (if provided) before the API call
        var refImageBase64: String? = null
        var refMimeType: String = "image/jpeg"
        if (referenceImageUri != null) {
            try {
                refMimeType = context.contentResolver.getType(referenceImageUri) ?: "image/jpeg"
                val bytes = context.contentResolver.openInputStream(referenceImageUri)?.use { it.readBytes() }
                if (bytes != null) {
                    refImageBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Build style context from reference image (uses Gemini Flash vision — always available)
        var styleContext: String? = null
        if (refImageBase64 != null && apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val analysisText = when (referenceMode) {
                    ImageReferenceMode.INSPIRE ->
                        "Describe the visual style, color palette, mood, lighting, and artistic technique of this image in 2 sentences. Focus only on style elements, not the content subject."
                    ImageReferenceMode.EDIT ->
                        "Describe the main subject, composition, layout, and key visual elements of this image in 2 sentences."
                }
                val capturedRef = refImageBase64
                val analysisRequest = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(
                        Part(text = analysisText),
                        Part(inlineData = InlineData(mimeType = refMimeType, data = capturedRef))
                    ))),
                    generationConfig = GenerationConfig(temperature = 0.2f)
                )
                val analysisResponse = apiService.generateContent(
                    model = "gemini-2.0-flash",
                    apiKey = apiKey,
                    request = analysisRequest
                )
                styleContext = analysisResponse.candidates
                    ?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val finalPrompt = when {
            styleContext != null && referenceMode == ImageReferenceMode.EDIT ->
                "Reinterpret this scene: $styleContext. Apply these changes: $enhancedPrompt"
            styleContext != null ->
                "$enhancedPrompt. Inspired by this visual style: $styleContext"
            else -> enhancedPrompt
        }

        // Step 1: Try Imagen 3 (requires billing on Google Cloud)
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val ratioParam = when (aspectRatio) {
                    AspectRatio.RATIO_1_1 -> "1:1"
                    AspectRatio.RATIO_9_16 -> "9:16"
                    AspectRatio.RATIO_16_9 -> "16:9"
                    AspectRatio.RATIO_4_3 -> "4:3"
                }
                val imagenRequest = ImagenGenerateRequest(
                    instances = listOf(ImagenInstance(prompt = finalPrompt)),
                    parameters = ImagenParameters(
                        sampleCount = 1,
                        aspectRatio = ratioParam,
                        negativePrompt = if (negativePrompt.isNullOrBlank()) null else negativePrompt
                    )
                )
                val imagenResponse = apiService.predict(
                    model = "imagen-3.0-generate-001",
                    apiKey = apiKey,
                    request = imagenRequest
                )
                val base64Data = imagenResponse.predictions?.firstOrNull()?.bytesBase64Encoded
                if (!base64Data.isNullOrBlank()) {
                    val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                    savedFilePath = saveBytesToFilesDir(bytes)
                    isRealGemini = true
                }
            } catch (e: Exception) {
                // 403 = no billing / no permission for Imagen 3 → fall through to Pollinations
                e.printStackTrace()
            }
        }

        // Step 2: Pollinations AI — free, no key needed, uses Flux model, matches prompt well
        if (savedFilePath == null) {
            try {
                val pollinationsBytes = fetchPollinationsImage(finalPrompt, aspectRatio)
                savedFilePath = saveBytesToFilesDir(pollinationsBytes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Step 3: Last resort contextual stock photo fallback
        if (savedFilePath == null) {
            val fallbackBytes = fetchContextualFallbackImage(parsedPrompt, style, aspectRatio)
            savedFilePath = saveBytesToFilesDir(fallbackBytes)
        }

        val artwork = GeneratedArtwork(
            prompt = prompt,
            negativePrompt = negativePrompt,
            style = style,
            aspectRatio = aspectRatio,
            quality = quality,
            timestamp = System.currentTimeMillis(),
            imageUri = savedFilePath!!,
            isFavorite = false,
            isDownloaded = isRealGemini // Real ones are auto-marked downloaded, fallback can be saved
        )

        val id = artworkDao.insertArtwork(ArtworkEntity.fromDomain(artwork))
        artwork.copy(id = id)
    }

    private fun fetchPollinationsImage(prompt: String, aspectRatio: AspectRatio): ByteArray {
        val (width, height) = when (aspectRatio) {
            AspectRatio.RATIO_1_1 -> Pair(1024, 1024)
            AspectRatio.RATIO_9_16 -> Pair(768, 1344)
            AspectRatio.RATIO_16_9 -> Pair(1344, 768)
            AspectRatio.RATIO_4_3 -> Pair(1152, 864)
        }
        val seed = (System.currentTimeMillis() % 99999).toInt()
        val encodedPrompt = URLEncoder.encode(prompt, "UTF-8")
        val url = "https://image.pollinations.ai/prompt/$encodedPrompt" +
                  "?width=$width&height=$height&model=flux&nologo=true&seed=$seed"

        val client = OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Android; Mobile)")
            .build()

        return client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                response.body?.bytes() ?: throw IOException("Empty response from Pollinations AI")
            } else {
                throw IOException("Pollinations AI error: ${response.code}")
            }
        }
    }

    private fun saveBytesToFilesDir(bytes: ByteArray): String {
        val fileName = "aiblend_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { fos ->
            fos.write(bytes)
            fos.flush()
        }
        return file.absolutePath
    }

    private fun fetchContextualFallbackImage(
        prompt: String,
        style: ImageStyle,
        aspectRatio: AspectRatio
    ): ByteArray {
        val client = OkHttpClient()
        val styleKeyword = style.displayName.lowercase()
        
        // Extract meaningful semantic nouns/adjectives from user prompt
        val keywords = prompt.split(" ", ",", ".", ";", "-")
            .map { it.trim().lowercase() }
            .filter { it.length > 3 && it !in listOf("with", "this", "that", "from", "into", "highly", "detailed", "realistic", "super", "beautiful", "gorgeous") }
            .take(3)
            .joinToString(",")

        val query = if (keywords.isNotEmpty()) "$styleKeyword,$keywords" else styleKeyword
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        
        // Dimension calculations based on selected aspect ratio
        val (width, height) = when (aspectRatio) {
            AspectRatio.RATIO_1_1 -> Pair(1000, 1000)
            AspectRatio.RATIO_9_16 -> Pair(720, 1280)
            AspectRatio.RATIO_16_9 -> Pair(1280, 720)
            AspectRatio.RATIO_4_3 -> Pair(1024, 768)
        }

        // LoremFlickr redirects to a beautiful matching dynamic image matching our styles
        val fallbackUrl = "https://loremflickr.com/$width/$height/$encodedQuery/all"

        return try {
            val request = Request.Builder()
                .url(fallbackUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                .build()
            
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.bytes() ?: createSolidColorPlaceholder(width, height)
                } else {
                    createSolidColorPlaceholder(width, height)
                }
            }
        } catch (e: IOException) {
            createSolidColorPlaceholder(width, height)
        }
    }

    // Completely offline visual builder if network is fully unavailable
    private fun createSolidColorPlaceholder(width: Int, height: Int): ByteArray {
        val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        
        // Premium subtle colorful neon purple gradient placeholder
        val paint = android.graphics.Paint()
        val gradient = android.graphics.LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            android.graphics.Color.parseColor("#1a0033"),
            android.graphics.Color.parseColor("#0b071e"),
            android.graphics.Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        
        // Neon grid overlay look
        paint.shader = null
        paint.color = android.graphics.Color.parseColor("#4400E5FF")
        paint.style = android.graphics.Paint.Style.STROKE
        paint.strokeWidth = 3f
        
        val spacing = 80f
        var x = 0f
        while (x < width) {
            canvas.drawLine(x, 0f, x, height.toFloat(), paint)
            x += spacing
        }
        var y = 0f
        while (y < height) {
            canvas.drawLine(0f, y, width.toFloat(), y, paint)
            y += spacing
        }

        val out = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
        return out.toByteArray()
    }
}
