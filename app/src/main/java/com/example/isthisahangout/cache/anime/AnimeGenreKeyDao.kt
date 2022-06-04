package com.example.isthisahangout.cache.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.AnimeByGenresRemoteKey

@Dao
interface AnimeGenreKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AnimeByGenresRemoteKey>)

    @Query("SELECT * FROM anime_by_genres_remote_key WHERE id = :id AND genre = :query")
    suspend fun getRemoteKeys(id: String, query: String): AnimeByGenresRemoteKey

    @Query("DELETE FROM anime_by_genres_remote_key WHERE genre = :query")
    suspend fun deleteRemoteKeys(query: String)
}