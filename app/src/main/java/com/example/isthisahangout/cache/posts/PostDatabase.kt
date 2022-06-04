package com.example.isthisahangout.cache.posts

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.isthisahangout.models.LikedPostId

@Database(
    entities = [LikedPostId::class], version = 5
)
@TypeConverters(Converters::class)
abstract class PostDatabase : RoomDatabase() {
    abstract fun getPostsDao(): PostsDao
}