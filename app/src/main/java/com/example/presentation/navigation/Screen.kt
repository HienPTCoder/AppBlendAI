package com.example.presentation.navigation

object Screen {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val GENERATE = "generate"
    const val GALLERY = "gallery"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    
    // Detailed image preview screen taking long id as path argument
    const val PREVIEW = "preview/{id}"
    
    fun createPreviewRoute(id: Long) = "preview/$id"
}
