package com.addavi.elify.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_songs")
    fun getAllFavorites(): Flow<List<FavoriteSong>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToFavorite(song: FavoriteSong)

    @Delete
    suspend fun removeFromFavorite(song: FavoriteSong)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE id = :songId)")
    fun isFavorite(songId: Long): Flow<Boolean>

}