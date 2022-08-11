package com.example.isthisahangout.cache.manga

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.*

@Database(
    entities = [
        MangaResults.Manga::class,
        RoomMangaByGenre::class,
        RoomMangaByGenreRemoteKey::class,
        MangaRemoteKey::class,
        MangaDetail::class
    ], version = 2
)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun getMangaDao(): MangaDao
    abstract fun getMangaRemoteKeyDao(): MangaRemoteKeyDao
    abstract fun getMangaDetailDao(): MangaDetailDao
}