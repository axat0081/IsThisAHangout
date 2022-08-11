package com.example.isthisahangout.cache.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.AnimeDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeDetail(animeDetail: AnimeDetail)

    @Query("SELECT COUNT(*) FROM anime_detail")
    suspend fun getAnimeDetailCount(): Int

    suspend fun insertAnimeDetailBounded(animeDetail: AnimeDetail) {
        val num = getAnimeDetailCount()
        if (num > 40) {
            deleteAnimeDetail()
        }
        insertAnimeDetail(animeDetail = animeDetail)
    }

    @Query("SELECT * FROM anime_detail WHERE id = :id")
    fun getAnimeDetail(id: Int): Flow<AnimeDetail?>

    @Query("DELETE FROM anime_detail")
    suspend fun deleteAnimeDetail()
}