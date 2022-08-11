package com.example.isthisahangout.models.hiddenContent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden_anime")
data class HiddenAnime(
    @PrimaryKey
    val name: String,
    val userId: String
)

@Entity(tableName = "hidden_manga")
data class HiddenManga(
    @PrimaryKey
    val name: String,
    val userId: String
)

@Entity(tableName = "hidden_video_game")
data class HiddenVideoGame(
    @PrimaryKey
    val name: String,
    val userId: String
)


