package com.example.isthisahangout.cache.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.AnimeSeasonResults
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeBySeasonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AnimeSeasonResults.RoomAnimeBySeason>)

    @Query("SELECT * FROM anime_by_season WHERE season = :season AND year = :year")
    fun getAnimeBySeason(
        season: String,
        year: String
    ): Flow<List<AnimeSeasonResults.RoomAnimeBySeason>>

    @Query("DELETE FROM anime_by_season where season = :season AND year = :year")
    fun deleteAll(season: String, year: String)
}