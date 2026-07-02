package com.addavi.elify.viewmodel

data class Song(
    val id: Long,
    val path: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val albumId: Long
)