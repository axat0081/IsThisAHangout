package com.example.isthisahangout.cache.favourites

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.favourites.FavAnime
import com.example.isthisahangout.models.favourites.FavGame
import com.example.isthisahangout.models.favourites.FavPost
import com.example.isthisahangout.models.favourites.FavVideo

@Database(
    entities = [
        FavAnime::class,
        FavGame::class,
        FavVideo::class,
        FavPost::class
    ], version = 2
)
abstract class FavouritesDatabase : RoomDatabase() {
    abstract fun getFavouritesDao(): FavouritesDao
}