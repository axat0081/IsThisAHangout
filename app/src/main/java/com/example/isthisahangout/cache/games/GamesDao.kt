package com.example.isthisahangout.cache.games

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.RoomGames

@Dao
interface GamesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<RoomGames>)

    @Query("SELECT * FROM games")
    fun getGames(): PagingSource<Int, RoomGames>

    @Query("DELETE FROM games")
    suspend fun deleteGames()
}