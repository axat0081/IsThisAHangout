package com.example.isthisahangout.cache.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.UpcomingAnimeRemoteKey

@Dao
interface UpcomingAnimeRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<UpcomingAnimeRemoteKey>)

    @Query("SELECT * FROM upcoming_anime_remote_key WHERE id = :id")
    suspend fun getRemoteKeys(id: String): UpcomingAnimeRemoteKey

    @Query("DELETE FROM upcoming_anime_remote_key")
    suspend fun deleteRemoteKeys()
}