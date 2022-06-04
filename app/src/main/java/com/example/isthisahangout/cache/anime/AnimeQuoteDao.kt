package com.example.isthisahangout.cache.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.RoomAnimeQuote
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeQuoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list:List<RoomAnimeQuote>)

    @Query("SELECT * FROM anime_quotes")
    fun getQuotes(): Flow<List<RoomAnimeQuote>>

    @Query("DELETE FROM anime_quotes")
    suspend fun delete()
}