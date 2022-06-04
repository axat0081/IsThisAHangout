package com.example.isthisahangout.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AnimeByNameResults(
    val results: List<AnimeByName>
) {
    @Entity(tableName = "anime_by_name")
    data class AnimeByName(
        @PrimaryKey
        @SerializedName("mal_id") val id: String,
        val title: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val url: String,
        val synopsis: String,
    )
}