package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AnimeSeasonResults(
    val anime: List<AnimeBySeason>
) {

    data class AnimeBySeason(
        @SerializedName("mal_id") val id: String,
        val title: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val url: String,
        val synopsis: String
    )

    @Parcelize
    @Entity(tableName = "anime_by_season")
    data class RoomAnimeBySeason(
        @PrimaryKey
        @SerializedName("mal_id") val id: String,
        val title: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val url: String,
        val synopsis: String,
        val season: String,
        val year: String,
        val updatedAt: Long = System.currentTimeMillis()
    ) : Parcelable
}