package com.example.domain.repository

import android.net.Uri
import com.example.domain.model.AspectRatio
import com.example.domain.model.GeneratedArtwork
import com.example.domain.model.ImageQuality
import com.example.domain.model.ImageReferenceMode
import com.example.domain.model.ImageStyle
import kotlinx.coroutines.flow.Flow

interface ArtworkRepository {
    fun getArtworksFlow(): Flow<List<GeneratedArtwork>>
    
    fun getFavoriteArtworksFlow(): Flow<List<GeneratedArtwork>>
    
    fun getDownloadedArtworksFlow(): Flow<List<GeneratedArtwork>>
    
    fun searchArtworksFlow(query: String): Flow<List<GeneratedArtwork>>
    
    suspend fun getArtworkById(id: Long): GeneratedArtwork?
    
    suspend fun saveArtwork(artwork: GeneratedArtwork): Long
    
    suspend fun deleteArtwork(id: Long)
    
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
    
    suspend fun markAsDownloaded(id: Long, isDownloaded: Boolean)
    
    suspend fun generateArtwork(
        prompt: String,
        negativePrompt: String?,
        style: ImageStyle,
        aspectRatio: AspectRatio,
        quality: ImageQuality,
        overrideApiKey: String?,
        referenceImageUri: Uri? = null,
        referenceMode: ImageReferenceMode = ImageReferenceMode.INSPIRE
    ): GeneratedArtwork
}
