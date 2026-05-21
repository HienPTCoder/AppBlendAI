package com.example.presentation.preview

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.GeneratedArtwork
import com.example.domain.repository.ArtworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

class PreviewViewModel(
    private val artworkRepository: ArtworkRepository,
    private val context: Context
) : ViewModel() {

    private val _currentArtwork = MutableStateFlow<GeneratedArtwork?>(null)
    val currentArtwork: StateFlow<GeneratedArtwork?> = _currentArtwork.asStateFlow()

    private val _saveStatus = MutableStateFlow<String?>(null)
    val saveStatus: StateFlow<String?> = _saveStatus.asStateFlow()

    fun loadArtwork(artworkId: Long) {
        viewModelScope.launch {
            val artwork = artworkRepository.getArtworkById(artworkId)
            _currentArtwork.value = artwork
        }
    }

    fun toggleFavorite() {
        val artwork = _currentArtwork.value ?: return
        viewModelScope.launch {
            val nextState = !artwork.isFavorite
            artworkRepository.toggleFavorite(artwork.id, nextState)
            _currentArtwork.value = artwork.copy(isFavorite = nextState)
        }
    }

    fun deleteArtwork(onComplete: () -> Unit) {
        val artwork = _currentArtwork.value ?: return
        viewModelScope.launch {
            artworkRepository.deleteArtwork(artwork.id)
            
            // Delete the private file if it exists
            try {
                val file = File(artwork.imageUri)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            onComplete()
        }
    }

    fun clearSaveStatus() {
        _saveStatus.value = null
    }

    // Modern android-safe media-store saving implementation
    fun saveToGallery() {
        val artwork = _currentArtwork.value ?: return
        _saveStatus.value = "Saving to Gallery..."
        
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                val sourceFile = File(artwork.imageUri)
                if (!sourceFile.exists()) return@withContext false

                val resolver = context.contentResolver
                val filename = "AI_Blend_${System.currentTimeMillis()}.jpg"

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AI_Blend")
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    try {
                        resolver.openOutputStream(imageUri).use { outputStream ->
                            if (outputStream != null) {
                                FileInputStream(sourceFile).use { inputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }
                        }
                        
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentValues.clear()
                            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                            resolver.update(imageUri, contentValues, null, null)
                        }
                        true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        false
                    }
                } else {
                    false
                }
            }

            if (success) {
                artworkRepository.markAsDownloaded(artwork.id, true)
                _currentArtwork.value = _currentArtwork.value?.copy(isDownloaded = true)
                _saveStatus.value = "Artwork successfully saved to Pictures/AI_Blend!"
            } else {
                _saveStatus.value = "Error: Failed to save artwork, please verify permissions."
            }
        }
    }

    fun shareImage() {
        val artwork = _currentArtwork.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(artwork.imageUri)
            if (!file.exists()) return@launch

            // To support safe standard sharing in modern Android, we can share file directly via FileProvider if set,
            // or use standard media store URI if saved, or a quick sharing stream.
            // Let's create an elegant temporary sharing intent using Android's built-in platform file share
            // Wait, we can share image data easily:
            try {
                // In modern android, sharing a filesDir file requires a FileProvider. To keep things 100% stable
                // and avoid any potential XML manifest FileProvider authority configurations crashing the app,
                // we can also save/share a media provider URI or launch a send action with text info and the private file path.
                // Let's build a standard Share Text and Image intent:
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_TITLE, "AI Blend Artwork")
                    putExtra(Intent.EXTRA_SUBJECT, "Check out my AI image: " + artwork.prompt)
                    putExtra(Intent.EXTRA_TEXT, "Generated with AI Blend: \"${artwork.prompt}\" Style: ${artwork.style.displayName}")
                    
                    // We can also attach the file URI directly, allowing local system copy
                    val fileUri = Uri.parse("file://${file.absolutePath}")
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                val chooser = Intent.createChooser(shareIntent, "Share AI Blend Artwork").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
