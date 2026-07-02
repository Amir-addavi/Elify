package com.addavi.elify.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(context: Context) {

    private val dao = MusicDatabase.getInstance(context).favoriteDao()

    fun getAllFavorites(): Flow<List<FavoriteSong>> = dao.getAllFavorites()

    suspend fun addToFavorite(song: FavoriteSong) = dao.addToFavorite(song)

    suspend fun removeFromFavorite(song: FavoriteSong) = dao.removeFromFavorite(song)

    fun isFavorite(songId: Long): Flow<Boolean> = dao.isFavorite(songId)

}