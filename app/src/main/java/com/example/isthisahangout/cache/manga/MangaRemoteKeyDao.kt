package com.example.isthisahangout.cache.manga

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.MangaRemoteKey
import com.example.isthisahangout.models.RoomMangaByGenreRemoteKey

@Dao
interface MangaRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMangaRemoteKey(keyList: List<MangaRemoteKey>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMangaByGenreRemoteKey(keyList: List<RoomMangaByGenreRemoteKey>)

    @Query("SELECT * FROM manga_remote_key WHERE id = :id")
    suspend fun getMangaRemoteKey(id: String): MangaRemoteKey

    @Query("SELECT * FROM manga_by_genre_remote_key WHERE id = :id AND genre = :query")
    suspend fun getMangaByGenreRemoteKey(id: String, query: String): RoomMangaByGenreRemoteKey

    @Query("DELETE FROM manga_remote_key")
    suspend fun deleteMangaRemoteKey()

    @Query("DELETE FROM manga_by_genre_remote_key WHERE genre = :query")
    suspend fun deleteMangaByGenreRemoteKey(query: String)

}