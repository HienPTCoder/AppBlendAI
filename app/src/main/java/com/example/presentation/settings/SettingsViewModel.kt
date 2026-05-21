package com.example.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore,
    private val context: Context
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = settingsDataStore.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val customApiKey: StateFlow<String?> = settingsDataStore.customApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val defaultQuality: StateFlow<String> = settingsDataStore.defaultQuality
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "STANDARD")

    val appLanguage: StateFlow<String> = settingsDataStore.appLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(enabled)
        }
    }

    fun saveCustomApiKey(key: String?) {
        viewModelScope.launch {
            settingsDataStore.setCustomApiKey(key)
        }
    }

    fun updateDefaultQuality(quality: String) {
        viewModelScope.launch {
            settingsDataStore.setDefaultQuality(quality)
        }
    }

    fun updateAppLanguage(lang: String) {
        viewModelScope.launch {
            settingsDataStore.setAppLanguage(lang)
        }
    }

    fun clearAppCache(onComplete: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val cacheDir = context.cacheDir
                val filesDir = context.filesDir
                var deletedCount = 0
                
                // Clear cache directory
                cacheDir.deleteRecursively()
                
                // We don't want to delete generated user artwork from filesDir, but can clear specific temporary files if any.
                onComplete("App cache cleared successfully!")
            } catch (e: Exception) {
                onComplete("Failed to clear some cache files: ${e.localizedMessage}")
            }
        }
    }
}
