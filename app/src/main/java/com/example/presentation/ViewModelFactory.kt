package com.example.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.AIBlendApplication
import com.example.presentation.generate.GenerateViewModel
import com.example.presentation.gallery.GalleryViewModel
import com.example.presentation.history.HistoryViewModel
import com.example.presentation.preview.PreviewViewModel
import com.example.presentation.settings.SettingsViewModel

class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    private val app = context.applicationContext as AIBlendApplication

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(app.settingsDataStore, context) as T
            }
            modelClass.isAssignableFrom(GenerateViewModel::class.java) -> {
                GenerateViewModel(app.artworkRepository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(app.artworkRepository) as T
            }
            modelClass.isAssignableFrom(GalleryViewModel::class.java) -> {
                GalleryViewModel(app.artworkRepository) as T
            }
            modelClass.isAssignableFrom(PreviewViewModel::class.java) -> {
                PreviewViewModel(app.artworkRepository, context) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
