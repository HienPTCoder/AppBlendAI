package com.example.presentation.history

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

class HistoryViewModel(
    private val artworkRepository: ArtworkRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortByFavorites = MutableStateFlow(false)
    val sortByFavorites: StateFlow<Boolean> = _sortByFavorites

    // Combine history source, search filters, and sort options reactively
    val historyItems: StateFlow<List<GeneratedArtwork>> = combine(
        artworkRepository.getArtworksFlow(),
        _searchQuery,
        _sortByFavorites
    ) { list, query, showFavoritesFirst ->
        val filtered = if (query.isBlank()) {
            list
        } else {
            list.filter { 
                it.prompt.contains(query, ignoreCase = true) || 
                it.style.displayName.contains(query, ignoreCase = true) 
            }
        }
        
        if (showFavoritesFirst) {
            filtered.sortedWith(
                compareByDescending<GeneratedArtwork> { it.isFavorite }
                    .thenByDescending { it.timestamp }
            )
        } else {
            filtered.sortedByDescending { it.timestamp }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSortByFavorites() {
        _sortByFavorites.value = !_sortByFavorites.value
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            artworkRepository.toggleFavorite(id, isFavorite)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            artworkRepository.deleteArtwork(id)
        }
    }
}
