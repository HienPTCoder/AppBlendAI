package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.AspectRatio
import com.example.domain.model.GeneratedArtwork
import com.example.domain.model.ImageQuality
import com.example.domain.model.ImageStyle

@Entity(tableName = "artworks")
data class ArtworkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prompt: String,
    val negativePrompt: String?,
    val style: String,
    val aspectRatio: String,
    val quality: String,
    val timestamp: Long,
    val imageUri: String,
    val isFavorite: Boolean,
    val isDownloaded: Boolean
) {
    fun toDomain(): GeneratedArtwork = GeneratedArtwork(
        id = id,
        prompt = prompt,
        negativePrompt = negativePrompt,
        style = ImageStyle.fromName(style),
        aspectRatio = AspectRatio.fromName(aspectRatio),
        quality = ImageQuality.fromName(quality),
        timestamp = timestamp,
        imageUri = imageUri,
        isFavorite = isFavorite,
        isDownloaded = isDownloaded
    )

    companion object {
        fun fromDomain(artwork: GeneratedArtwork) = ArtworkEntity(
            id = artwork.id,
            prompt = artwork.prompt,
            negativePrompt = artwork.negativePrompt,
            style = artwork.style.name,
            aspectRatio = artwork.aspectRatio.name,
            quality = artwork.quality.name,
            timestamp = artwork.timestamp,
            imageUri = artwork.imageUri,
            isFavorite = artwork.isFavorite,
            isDownloaded = artwork.isDownloaded
        )
    }
}
