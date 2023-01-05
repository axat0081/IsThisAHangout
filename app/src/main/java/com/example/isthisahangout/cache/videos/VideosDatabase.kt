package com.example.isthisahangout.cache.videos

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.LikedVideoId

@Database(entities = [LikedVideoId::class], version = 1)
abstract class VideosDatabase : RoomDatabase() {
    abstract fun getVideosDao(): VideosDao
}