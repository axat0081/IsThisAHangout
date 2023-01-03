package com.example.isthisahangout.models.favourites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga_favourites")
data class FavManga(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val image: String
)