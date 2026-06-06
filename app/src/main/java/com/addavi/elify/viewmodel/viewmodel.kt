package com.addavi.elify.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
class MusicViewModel(
    private val context: Context
) : ViewModel() {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _randomSongs = MutableStateFlow<List<Song>>(emptyList())
    val randomSongs: StateFlow<List<Song>> = _randomSongs.asStateFlow()

    val songCount: StateFlow<Int> = _songs
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )


    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            val loadedSongs = getAllSongs(context)
            _songs.value = loadedSongs
            generateRandomSongs(loadedSongs)
        }
    }


    private fun generateRandomSongs(currentList: List<Song> = _songs.value) {
        if (currentList.isEmpty()) return

        val filteredList = currentList.filter { it.artworkUri != null }
        _randomSongs.value = if (currentList.size >= 7) {
            filteredList.shuffled().take(7)
        } else {
            currentList
        }
    }
}
