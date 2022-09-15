package com.example.isthisahangout.cache.movies

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.movies.MovieEntity
import com.example.isthisahangout.models.movies.MoviesRemoteKey

@Database(
    entities = [
        MovieEntity::class,
        MoviesRemoteKey::class
    ],
    version = 1
)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun getMoviesDao(): MoviesDao
}