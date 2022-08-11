package com.example.isthisahangout.cache.hiddenContent

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.hiddenContent.HiddenAnime
import com.example.isthisahangout.models.hiddenContent.HiddenManga
import com.example.isthisahangout.models.hiddenContent.HiddenVideoGame

@Database(
    entities = [
        HiddenAnime::class,
        HiddenManga::class,
        HiddenVideoGame::class
    ],
    version = 1
)
abstract class HiddenContentDatabase : RoomDatabase() {
    abstract fun getHiddenContentDao(): HiddenContentDao
}