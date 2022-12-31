package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.isthisahangout.models.util.Images
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class AnimeGenreResults(
    val data: List<AnimeByGenresDto>
) {

    data class AnimeByGenresDto(
        @SerializedName("mal_id") val id: String,
        val title: String,
        val images: Images,
        val url: String,
        val synopsis: String,
        val favorites: Int?
    )

    @Parcelize
    data class AnimeByGenres(
        @SerializedName("mal_id") val id: String,
        val title: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val url: String,
        val synopsis: String,
        val favorites: Int
    ) : Parcelable
}

fun AnimeGenreResults.AnimeByGenresDto.toAnimeByGenres(): AnimeGenreResults.AnimeByGenres =
    AnimeGenreResults.AnimeByGenres(
        id = id,
        title = title,
        imageUrl = images.jpg.image_url,
        url = url,
        synopsis = synopsis,
        favorites = favorites ?: 0
    )

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
    val genre: String,
    val favorites: Int
) : Parcelable

@Entity(tableName = "anime_by_genres_remote_key")
data class AnimeByGenresRemoteKey(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val genre: String
)