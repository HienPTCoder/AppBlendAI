package com.example.presentation.generate

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.AspectRatio
import com.example.domain.model.GeneratedArtwork
import com.example.domain.model.ImageQuality
import com.example.domain.model.ImageReferenceMode
import com.example.domain.model.ImageStyle
import com.example.domain.repository.ArtworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GenerationState {
    object Idle : GenerationState
    data class Loading(val message: String) : GenerationState
    data class Success(val artwork: GeneratedArtwork) : GenerationState
    data class Error(val message: String) : GenerationState
}

class GenerateViewModel(
    private val artworkRepository: ArtworkRepository
) : ViewModel() {

    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _negativePrompt = MutableStateFlow("")
    val negativePrompt: StateFlow<String> = _negativePrompt.asStateFlow()

    private val _selectedStyle = MutableStateFlow(ImageStyle.REALISTIC)
    val selectedStyle: StateFlow<ImageStyle> = _selectedStyle.asStateFlow()

    private val _selectedAspectRatio = MutableStateFlow(AspectRatio.RATIO_1_1)
    val selectedAspectRatio: StateFlow<AspectRatio> = _selectedAspectRatio.asStateFlow()

    private val _selectedQuality = MutableStateFlow(ImageQuality.STANDARD)
    val selectedQuality: StateFlow<ImageQuality> = _selectedQuality.asStateFlow()

    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    private val _referenceImageUri = MutableStateFlow<Uri?>(null)
    val referenceImageUri: StateFlow<Uri?> = _referenceImageUri.asStateFlow()

    private val _referenceMode = MutableStateFlow(ImageReferenceMode.INSPIRE)
    val referenceMode: StateFlow<ImageReferenceMode> = _referenceMode.asStateFlow()

    fun updatePrompt(value: String) {
        _prompt.value = value
    }

    fun updateNegativePrompt(value: String) {
        _negativePrompt.value = value
    }

    fun selectStyle(style: ImageStyle) {
        _selectedStyle.value = style
    }

    fun selectAspectRatio(ratio: AspectRatio) {
        _selectedAspectRatio.value = ratio
    }

    fun selectQuality(quality: ImageQuality) {
        _selectedQuality.value = quality
    }

    fun setReferenceImage(uri: Uri?) {
        _referenceImageUri.value = uri
        if (uri == null) _referenceMode.value = ImageReferenceMode.INSPIRE
    }

    fun setReferenceMode(mode: ImageReferenceMode) {
        _referenceMode.value = mode
    }

    fun setGenerationState(state: GenerationState) {
        _generationState.value = state
    }

    fun populateFromPrompt(promptText: String, negPrompt: String?, style: ImageStyle, ratio: AspectRatio, qual: ImageQuality) {
        _prompt.value = promptText
        _negativePrompt.value = negPrompt ?: ""
        _selectedStyle.value = style
        _selectedAspectRatio.value = ratio
        _selectedQuality.value = qual
    }

    fun generateImage(overrideApiKey: String?) {
        val currentPrompt = _prompt.value.trim()
        if (currentPrompt.isEmpty()) {
            _generationState.value = GenerationState.Error("Prompt cannot be empty")
            return
        }

        viewModelScope.launch {
            _generationState.value = GenerationState.Loading("Polishing prompt descriptions...")
            try {
                // Introduce slight delaying steps to simulate complex progressive generation mechanics beautifully
                _generationState.value = GenerationState.Loading("Contacting Gemini neural networks...")
                
                val artwork = artworkRepository.generateArtwork(
                    prompt = currentPrompt,
                    negativePrompt = if (_negativePrompt.value.isBlank()) null else _negativePrompt.value,
                    style = _selectedStyle.value,
                    aspectRatio = _selectedAspectRatio.value,
                    quality = _selectedQuality.value,
                    overrideApiKey = overrideApiKey,
                    referenceImageUri = _referenceImageUri.value,
                    referenceMode = _referenceMode.value
                )
                
                _generationState.value = GenerationState.Success(artwork)
            } catch (e: Exception) {
                _generationState.value = GenerationState.Error(e.localizedMessage ?: "Generation timed out. Please check network.")
            }
        }
    }

    fun resetState() {
        _generationState.value = GenerationState.Idle
    }

    fun generateRandomPrompt(): String {
        val prompts = listOf(
            "An astronaut playing electric guitar on top of a futuristic purple neon volcano",
            "A cosmic cat looking into a fishbowl of tiny swimming galaxies, vibrant colors",
            "Rainy neon cyberpunk street in Neo-Tokyo, reflection of high tech skyscrapers",
            "A majestic tree of life growing mythical crystals under starry nebula skies",
            "A high-detailed futuristic mechanical owl with glowing cyan camera eyes on a mahogany branch",
            "An ancient stone castle floating on cloud islands with golden waterfall streams cascading down",
            "A cute fluffy red panda wearing a steampunk pilot suit and round brass brass goggles"
        )
        val selected = prompts.random()
        _prompt.value = selected
        return selected
    }
}
