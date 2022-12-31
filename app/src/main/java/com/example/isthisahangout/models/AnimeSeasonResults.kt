package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.isthisahangout.models.util.Images
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AnimeSeasonsResults(
    val data: List<AnimeBySeasonsDto>
) {

    data class AnimeBySeasonsDto(
        @SerializedName("mal_id") val id: String,
        val title: String,
        val images: Images,
        val url: String,
        val synopsis: String,
        val favorites: Int?
    )

    @Parcelize
    data class AnimeBySeasons(
        @SerializedName("mal_id") val id: String,
        val title: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val url: String,
        val synopsis: String,
        val favorites: Int
    ) : Parcelable
}

fun AnimeSeasonsResults.AnimeBySeasonsDto.toAnimeBySeasons(): AnimeSeasonsResults.AnimeBySeasons =
    AnimeSeasonsResults.AnimeBySeasons(
        id = id,
        title = title,
        imageUrl = images.jpg.image_url,
        url = url,
        synopsis = synopsis,
        favorites = favorites ?: 0
    )

@Parcelize
@Entity(tableName = "anime_by_seasons")
data class RoomAnimeBySeasons(
    @PrimaryKey
    @SerializedName("mal_id") val id: String,
    val title: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val url: String,
    val synopsis: String,
    val favorites: Int,
    val season: String,
    val year: String
) : Parcelable

@Entity(tableName = "anime_by_seasons_remote_key")
data class AnimeBySeasonsRemoteKey(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val season: String,
    val year: String
)