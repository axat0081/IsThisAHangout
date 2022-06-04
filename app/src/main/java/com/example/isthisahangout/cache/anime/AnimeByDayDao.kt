package com.example.isthisahangout.cache.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.isthisahangout.models.RoomAnimeByDay
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeByDayDao {
    @Query("SELECT * FROM anime_by_day WHERE day = :day")
    fun getAnimeByDay(day: String): Flow<List<RoomAnimeByDay>>

    @Insert
    suspend fun insertAnimeByDay(anime: List<RoomAnimeByDay>)

    @Query("DELETE FROM anime_by_day WHERE day = :day")
    suspend fun deleteAnimeByDay(day: String)
}