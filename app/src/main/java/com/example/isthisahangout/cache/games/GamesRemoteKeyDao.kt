package com.example.isthisahangout.cache.games

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.GameRemoteKey

@Dao
interface GamesRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<GameRemoteKey>)

    @Query("SELECT * FROM games_remote_key where id = :id")
    suspend fun getRemoteKeys(id: String): GameRemoteKey

    @Query("DELETE FROM games_remote_key")
    suspend fun deleteGameRemoteKey()
}