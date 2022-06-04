package com.example.isthisahangout.cache.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.AnimeImage
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimePicsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeImages(images: List<AnimeImage>)

    @Query("SELECT * FROM anime_pics")
    fun getAnimeImages(): Flow<List<AnimeImage>>

    @Query("DELETE FROM anime_pics")
    suspend fun deleteFromAnimeImages()

}