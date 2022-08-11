package com.example.isthisahangout.cache.manga

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.MangaDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMangaDetail(mangaDetail: MangaDetail)

    @Query("SELECT COUNT(*) FROM manga_detail")
    suspend fun getMangaDetailCount(): Int

    suspend fun insertMangaDetailBounded(mangaDetail: MangaDetail) {
        val num = getMangaDetailCount()
        if (num > 40) {
            deleteMangaDetail()
        }
        insertMangaDetail(mangaDetail)
    }

    @Query("SELECT * FROM manga_detail WHERE id = :id")
    fun getMangaDetail(id: Int): Flow<MangaDetail?>

    @Query("DELETE FROM manga_detail")
    suspend fun deleteMangaDetail()
}