package com.addavi.elify.viewmodel

import android.content.Context
import android.provider.MediaStore
import com.addavi.elify.components.getAlbumArtUri
import kotlin.collections.plusAssign
import kotlin.io.use


fun getAllSongs(context: Context): List<Song> {
    val songList = mutableListOf<Song>()

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM_ID
    )

    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

    context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        null,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

        while (cursor.moveToNext()) {
            val albumId = cursor.getLong(albumIdColumn)
            val song = Song(
                id = cursor.getLong(idColumn),
                title = cursor.getString(titleColumn),
                artist = cursor.getString(artistColumn),
                album = cursor.getString(albumColumn),
                data = cursor.getString(dataColumn),
                duration = cursor.getLong(durationColumn),
                artworkUri = getAlbumArtUri(albumId)
            )
            songList += song
        }
    }

    return songList
}
