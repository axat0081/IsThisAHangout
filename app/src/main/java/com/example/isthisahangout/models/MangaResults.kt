package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MangaResults(
    val top: List<Manga>
) {
    @Parcelize
    @Entity(tableName = "manga_table")
    data class Manga(
        @PrimaryKey
        @SerializedName("mal_id")
        val id: String,
        val title: String,
        val url: String,
        @SerializedName("image_url")
        val imageUrl: String,
        @SerializedName("start_date")
        val startDate: String
    ) : Parcelable
}

data class MangaGenreResults(
    val results: List<MangaByGenre>
) {
    data class MangaByGenre(
        @SerializedName("mal_id")
        val id: String,
        val title: String,
        val url: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val synopsis: String,
        @SerializedName("start_date")
        val startDate: String
    )
}

@Parcelize
@Entity(tableName = "manga_by_genre_table")
data class RoomMangaByGenre(
    @PrimaryKey
    @SerializedName("mal_id")
    val id: String,
    val title: String,
    val url: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val synopsis: String,
    val genre: String,
    val startDate: String
) : Parcelable

@Entity(tableName = "manga_remote_key")
data class MangaRemoteKey(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)

@Entity(tableName = "manga_by_genre_remote_key")
data class RoomMangaByGenreRemoteKey(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val genre: String
)