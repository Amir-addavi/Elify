package com.addavi.elify.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.addavi.elify.viewmodel.Song

class PlayerViewModel : ViewModel() {

    var currentSong by mutableStateOf<Song?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    fun playSong(song: Song) {

        currentSong = song

        isPlaying = true
    }
}
