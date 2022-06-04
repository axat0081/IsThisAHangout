package com.example.isthisahangout.cache.chat

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.Message

@Database(entities = [Message::class], version = 1)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun getMessagesDao(): ChatDao
}