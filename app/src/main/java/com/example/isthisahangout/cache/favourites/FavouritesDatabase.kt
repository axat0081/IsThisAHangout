package com.example.isthisahangout.cache.favourites

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.favourites.*

@Database(
    entities = [
        FavAnime::class,
        FavManga::class,
        FavGame::class,
        FavVideo::class,
        FavPost::class
    ], version = 3
)
abstract class FavouritesDatabase : RoomDatabase() {
    abstract fun getFavouritesDao(): FavouritesDao
}