package com.addavi.elify.player

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.addavi.elify.ui.theme.extractDominantColorFromUri
import com.addavi.elify.viewmodel.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(private val context: Context) : ViewModel() {

    var currentSong by mutableStateOf<Song?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    private var playlist = listOf<Song>()

    fun setPlaylist(songs: List<Song>) {
        playlist = songs
    }

    fun playSong(song: Song) {
        currentSong = song
        isPlaying = true
        loadDominantColor(song.albumId)
    }

    fun nextSong() {
        val currentIndex = playlist.indexOfFirst { it.id == currentSong?.id }
        if (currentIndex < playlist.size - 1) {
            currentSong = playlist[currentIndex + 1]
            loadDominantColor(currentSong?.albumId ?: 0)
        }
    }

    fun previousSong() {
        val currentIndex = playlist.indexOfFirst { it.id == currentSong?.id }
        if (currentIndex > 0) {
            currentSong = playlist[currentIndex - 1]
            loadDominantColor(currentSong?.albumId ?: 0)
        }
    }

    var isDriveMode by mutableStateOf(false)
        private set

    fun toggleDriveMode() {
        isDriveMode = !isDriveMode
    }
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()

    var volume by mutableStateOf(
        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxVolume
    )
        private set

    fun updateVolume(value: Float) {
        volume = value.coerceIn(0f, 1f)
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (value * maxVolume).toInt(),
            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
        )
    }




    private val maxCacheSize = 50

    private val colorCache = object : LinkedHashMap<Long, Color>(maxCacheSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Long, Color>?): Boolean {
            return size > maxCacheSize
        }
    }

    private val _dominantColor = MutableStateFlow(Color(0xFF6B4EFF))
    val dominantColor: StateFlow<Color> = _dominantColor.asStateFlow()

    fun loadDominantColor(albumId: Long) {
        colorCache[albumId]?.let {
            _dominantColor.value = it
            return
        }

        viewModelScope.launch {
            val uri = Uri.parse("content://media/external/audio/albumart/$albumId")
            val color = extractDominantColorFromUri(context, uri)
            colorCache[albumId] = color
            _dominantColor.value = color
        }
    }
}