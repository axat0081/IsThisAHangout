package com.example.isthisahangout.cache.anime

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.UpcomingAnimeResponse

@Dao
interface UpcomingAnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<UpcomingAnimeResponse.UpcomingAnime>)

    @Query("SELECT * FROM upcoming_anime")
    fun getAnime(): PagingSource<Int, UpcomingAnimeResponse.UpcomingAnime>

    @Query("DELETE FROM upcoming_anime")
    suspend fun deleteAnime()
}