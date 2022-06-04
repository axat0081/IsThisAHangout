package com.example.isthisahangout.cache.anime

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.AiringAnimeResponse

@Dao
interface AiringAnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AiringAnimeResponse.AiringAnime>)

    @Query("SELECT * FROM airing_anime")
    fun getAnime(): PagingSource<Int, AiringAnimeResponse.AiringAnime>

    @Query("DELETE FROM airing_anime")
    suspend fun deleteAnime()
}