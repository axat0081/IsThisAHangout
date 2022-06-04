package com.example.isthisahangout.cache.manga

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.MangaResults
import com.example.isthisahangout.models.RoomMangaByGenre

@Dao
interface MangaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(manga: List<MangaResults.Manga>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomMangaByGenre(manga: List<RoomMangaByGenre>)

    @Query("SELECT * FROM manga_table")
    fun getManga(): PagingSource<Int, MangaResults.Manga>

    @Query("SELECT * FROM manga_by_genre_table WHERE genre = :query")
    fun getRoomMangaByGenre(query: String): PagingSource<Int, RoomMangaByGenre>

    @Query("DELETE FROM manga_table")
    suspend fun deleteManga()

    @Query("DELETE FROM manga_by_genre_table WHERE genre =:query")
    suspend fun deleteMangaByGenre(query: String)
}