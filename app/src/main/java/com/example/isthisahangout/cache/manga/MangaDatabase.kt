package com.example.isthisahangout.cache.manga

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.MangaRemoteKey
import com.example.isthisahangout.models.MangaResults
import com.example.isthisahangout.models.RoomMangaByGenre
import com.example.isthisahangout.models.RoomMangaByGenreRemoteKey

@Database(
    entities = [
        MangaResults.Manga::class,
        RoomMangaByGenre::class,
        RoomMangaByGenreRemoteKey::class,
        MangaRemoteKey::class
    ], version = 2
)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun getMangaDao(): MangaDao
    abstract fun getMangaRemoteKeyDao(): MangaRemoteKeyDao
}