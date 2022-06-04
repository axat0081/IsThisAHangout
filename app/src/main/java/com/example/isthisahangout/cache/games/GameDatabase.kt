package com.example.isthisahangout.cache.games

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.isthisahangout.models.GameRemoteKey

import com.example.isthisahangout.models.RoomGames

@Database(
    entities = [RoomGames::class,
        GameRemoteKey::class,
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract fun getGamesDao(): GamesDao
    abstract fun getGamesRemoteKeyDao(): GamesRemoteKeyDao
}
