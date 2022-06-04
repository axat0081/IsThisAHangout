package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class AnimeGenreResults(
    val results: List<AnimeByGenres>
) {
    @Parcelize
    data class AnimeByGenres(
        @SerializedName("mal_id") val id: String,
        val title: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val url: String,
        val synopsis: String,
    ):Parcelable
}

@Parcelize
@Entity(tableName = "anime_by_genres")
data class RoomAnimeByGenres(
    @PrimaryKey
    @SerializedName("mal_id") val id: String,
    val title: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val url: String,
    val synopsis: String,
    val genre: String
) : Parcelable

@Entity(tableName = "anime_by_genres_remote_key")
data class AnimeByGenresRemoteKey(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val genre: String
)