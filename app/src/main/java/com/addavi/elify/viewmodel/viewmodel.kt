package com.addavi.elify.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.addavi.elify.data.FavoriteRepository
import com.addavi.elify.data.FavoriteSong
import com.addavi.elify.ui.theme.ThemePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class MusicViewModel(
    context: Context
) : ViewModel() {

    private val repository = MusicRepository(context)
    private val favoriteRepository = FavoriteRepository(context)

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _randomSongs = MutableStateFlow<List<Song>>(emptyList())
    val randomSongs: StateFlow<List<Song>> = _randomSongs.asStateFlow()

    private val _isLoading = MutableStateFlow(true)

    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    fun loadSongs() {
        if (_songs.value.isNotEmpty()) return // ✅ از لود مجدد جلوگیری کن

        viewModelScope.launch {
            _isLoading.value = true
            val loadedSongs = repository.getSongs()
            _songs.value = loadedSongs
            generateRandomSongs(loadedSongs)
            _isLoading.value = false
        }
    }

    val songCount: StateFlow<Int> = _songs
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val favorites: StateFlow<List<FavoriteSong>> = favoriteRepository.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addToFavorite(song: Song) {
        viewModelScope.launch {
            favoriteRepository.addToFavorite(FavoriteSong(song.id))
        }
    }

    fun removeFromFavorite(song: Song) {
        viewModelScope.launch {
            favoriteRepository.removeFromFavorite(FavoriteSong(song.id))
        }
    }

    fun isFavorite(songId: Long): Flow<Boolean> = favoriteRepository.isFavorite(songId)



    private fun generateRandomSongs(currentList: List<Song>) {
        if (currentList.isEmpty()) return
        _randomSongs.value =
            if (currentList.size >= 7)
                currentList.shuffled().take(7)
            else
                currentList
    }

    private val themePreferences = ThemePreferences(context)

    val isDarkTheme: StateFlow<Boolean> = themePreferences.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            themePreferences.setDarkTheme(isDark)
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<Song>> = _searchQuery
        .debounce(300)
        .combine(_songs) { query, songs ->
            if (query.isBlank()) emptyList()
            else songs.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                        song.artist.contains(query, ignoreCase = true) ||
                        song.album.contains(query, ignoreCase = true)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}