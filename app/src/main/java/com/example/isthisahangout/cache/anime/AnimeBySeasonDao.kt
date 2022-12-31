package com.example.isthisahangout.cache.anime

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.RoomAnimeBySeasons

@Dao
interface AnimeBySeasonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<RoomAnimeBySeasons>)

    @Query("SELECT * FROM anime_by_seasons WHERE season = :season AND year = :year")
    fun getAnimeBySeason(
        season: String,
        year: String
    ): PagingSource<Int, RoomAnimeBySeasons>

    @Query("DELETE FROM anime_by_seasons where season = :season AND year = :year")
    fun deleteAll(season: String, year: String)
}