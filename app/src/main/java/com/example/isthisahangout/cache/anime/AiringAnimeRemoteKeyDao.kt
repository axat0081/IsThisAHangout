package com.example.isthisahangout.cache.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.AiringAnimeRemoteKey

@Dao
interface AiringAnimeRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AiringAnimeRemoteKey>)

    @Query("SELECT * FROM airing_anime_remote_key WHERE id = :id")
    suspend fun getRemoteKeys(id: String): AiringAnimeRemoteKey

    @Query("DELETE FROM airing_anime_remote_key")
    suspend fun deleteRemoteKeys()
}