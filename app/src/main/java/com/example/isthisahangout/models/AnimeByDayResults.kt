package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AnimeByDayResults(
    val monday: List<AnimeByDay>,
    val tuesday: List<AnimeByDay>,
    val wednesday: List<AnimeByDay>,
    val thursday: List<AnimeByDay>,
    val friday: List<AnimeByDay>,
    val saturday: List<AnimeByDay>,
    val sunday: List<AnimeByDay>

) {

    data class AnimeByDay(
        @SerializedName("mal_id") val id: String,
        val title: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val url: String,
        val synopsis: String,
    )
}

@Parcelize
@Entity(tableName = "anime_by_day")
data class RoomAnimeByDay(
    @PrimaryKey
    @SerializedName("mal_id") val id: String,
    val title: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val url: String,
    val synopsis: String,
    val day: String
):Parcelable