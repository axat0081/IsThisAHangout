package com.example.isthisahangout.cache.anime

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.RoomAnimeByGenres

@Dao
interface AnimeGenreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<RoomAnimeByGenres>)

    @Query("SELECT * FROM anime_by_genres where genre = :query")
    fun getAnimeByGenre(query: String): PagingSource<Int, RoomAnimeByGenres>

    @Query("DELETE FROM anime_by_genres where genre = :query")
    suspend fun deleteAnimeByGenre(query: String)
}