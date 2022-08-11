package com.example.isthisahangout.ui.models

import androidx.room.PrimaryKey

sealed class VideoGameUIModel {
    data class VideoGameModel(
        @PrimaryKey val id: String,
        val name: String?,
        val imageUrl: String?,
        val rating: String?,
        val genres: ArrayList<String?>,
        val screenshots: ArrayList<String?>
    ): VideoGameUIModel()
    data class VideoGameSeparator(val desc: String): VideoGameUIModel()
}