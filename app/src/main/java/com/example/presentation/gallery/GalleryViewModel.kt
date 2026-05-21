package com.example.presentation.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.GeneratedArtwork
import com.example.domain.repository.ArtworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class GalleryFilter {
    ALL, FAVORITES, DOWNLOADS
}

class GalleryViewModel(
    private val artworkRepository: ArtworkRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(GalleryFilter.ALL)
    val selectedFilter: StateFlow<GalleryFilter> = _selectedFilter

    val galleryItems: StateFlow<List<GeneratedArtwork>> = combine(
        artworkRepository.getArtworksFlow(),
        _selectedFilter
    ) { list, filter ->
        when (filter) {
            GalleryFilter.ALL -> list
            GalleryFilter.FAVORITES -> list.filter { it.isFavorite }
            GalleryFilter.DOWNLOADS -> list.filter { it.isDownloaded }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectFilter(filter: GalleryFilter) {
        _selectedFilter.value = filter
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            artworkRepository.toggleFavorite(id, isFavorite)
        }
    }

    fun deleteArtwork(id: Long) {
        viewModelScope.launch {
            artworkRepository.deleteArtwork(id)
        }
    }
}
