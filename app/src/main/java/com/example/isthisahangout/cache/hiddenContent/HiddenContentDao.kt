package com.example.isthisahangout.cache.hiddenContent

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.hiddenContent.HiddenAnime
import com.example.isthisahangout.models.hiddenContent.HiddenManga
import com.example.isthisahangout.models.hiddenContent.HiddenVideoGame
import kotlinx.coroutines.flow.Flow

@Dao
interface HiddenContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHiddenAnime(anime: HiddenAnime)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHiddenManga(manga: HiddenManga)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHiddenVideoGame(videoGame: HiddenVideoGame)

    @Query("SELECT * FROM hidden_anime WHERE userId = :userId")
    fun getHiddenAnime(userId: String): Flow<List<HiddenAnime>>

    @Query("SELECT * FROM hidden_manga WHERE userId = :userId")
    fun getHiddenManga(userId: String): Flow<List<HiddenManga>>

    @Query("SELECT * FROM hidden_video_game WHERE userId = :userId")
    fun getHiddenVideoGame(userId: String): Flow<List<HiddenVideoGame>>

    @Delete
    suspend fun deleteHiddenAnime(anime: HiddenAnime)

    @Delete
    suspend fun deleteHiddenManga(manga: HiddenManga)

    @Delete
    suspend fun deleteHiddenVideoGame(videoGame: HiddenVideoGame)
}