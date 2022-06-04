package com.example.isthisahangout.models.favourites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_favourites")
data class FavAnime(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val title: String,
    val image: String
)