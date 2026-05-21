package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ai_blend_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_CUSTOM_API_KEY = stringPreferencesKey("custom_api_key")
        private val KEY_DEFAULT_QUALITY = stringPreferencesKey("default_quality")
        private val KEY_APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_DARK_MODE] ?: true // default to dark mode for neon aesthetic!
    }

    val customApiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_CUSTOM_API_KEY]
    }

    val defaultQuality: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_DEFAULT_QUALITY] ?: "STANDARD"
    }

    val appLanguage: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_APP_LANGUAGE] ?: "en"
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = enabled
        }
    }

    suspend fun setCustomApiKey(key: String?) {
        context.dataStore.edit { preferences ->
            if (key.isNullOrBlank()) {
                preferences.remove(KEY_CUSTOM_API_KEY)
            } else {
                preferences[KEY_CUSTOM_API_KEY] = key
            }
        }
    }

    suspend fun setDefaultQuality(quality: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_QUALITY] = quality
        }
    }

    suspend fun setAppLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_APP_LANGUAGE] = language
        }
    }
}
