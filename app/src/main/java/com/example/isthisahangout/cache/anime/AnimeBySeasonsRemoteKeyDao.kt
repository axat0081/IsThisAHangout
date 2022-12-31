package com.example.isthisahangout.cache.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.AnimeBySeasonsRemoteKey

@Dao
interface AnimeBySeasonsRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AnimeBySeasonsRemoteKey>)

    @Query("SELECT * FROM anime_by_seasons_remote_key WHERE id = :id AND season = :season AND year = :year")
    suspend fun getRemoteKeys(id: String, season: String, year: String): AnimeBySeasonsRemoteKey

    @Query("DELETE FROM anime_by_seasons_remote_key WHERE season = :season AND year = :year")
    suspend fun deleteRemoteKeys(year: String, season: String)

}