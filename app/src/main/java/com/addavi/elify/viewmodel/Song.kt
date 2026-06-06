package com.addavi.elify.viewmodel

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String?,
    val album: String?,
    val data: String,
    val duration: Long,
    val artworkUri: Uri?
)