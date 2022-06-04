package com.example.isthisahangout.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_news")
data class AnimeNews(
    val image: String,
    @PrimaryKey
    val title: String,
    val author: String,
    val desc: String,
    val url: String
)