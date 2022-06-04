package com.example.isthisahangout.models

import androidx.room.Entity
import androidx.room.PrimaryKey


data class AnimeQuote(
    val anime: String,
    val quote: String,
    val character: String
)

@Entity(tableName = "anime_quotes")
data class RoomAnimeQuote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val anime: String,
    val quote: String,
    val character: String,
    val updatedAt: Long = System.currentTimeMillis()
)
