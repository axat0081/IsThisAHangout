package com.example.isthisahangout.cache.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.AnimeNews
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeNewsDao {

    @Query("SELECT * FROM anime_news")
    fun getAnimeNews(): Flow<List<AnimeNews>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeNews(animeNews: List<AnimeNews>)

    @Query("DELETE FROM anime_news")
    suspend fun deleteAnimeNews()

}