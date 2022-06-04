package com.example.isthisahangout.cache.chat

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.Message

@Dao
interface ChatDao {

    @Query("SELECT * FROM messages")
    fun getMessagesPaged(): PagingSource<Int, Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(message: List<Message>)

    @Query("SELECT * FROM messages ORDER BY time LIMIT 1")
    suspend fun getLastMessage(): Message?
}