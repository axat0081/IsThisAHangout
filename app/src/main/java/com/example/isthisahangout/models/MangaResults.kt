package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.isthisahangout.models.util.Images
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MangaResults(
    val data: List<MangaDto>
) {

    data class MangaDto(
        @SerializedName("mal_id")
        val id: String,
        val title: String,
        val url: String,
        val images: Images,
        val favorites: Int?
    )

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
        val favorites: Int
    ) : Parcelable
}

data class MangaGenreResults(
    val data: List<MangaByGenreDto>
) {
    data class MangaByGenreDto(
        @SerializedName("mal_id")
        val id: String,
        val title: String,
        val url: String,
        val images: Images,
        val synopsis: String,
        val favorites: Int?
    )

    data class MangaByGenre(
        @SerializedName("mal_id")
        val id: String,
        val title: String,
        val url: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val synopsis: String,
        val favorites: Int
    )
}

fun MangaResults.MangaDto.toManga(): MangaResults.Manga =
    MangaResults.Manga(
        id = id,
        title = title,
        url = url,
        imageUrl = images.jpg.image_url,
        favorites = favorites ?: 0
    )

fun MangaGenreResults.MangaByGenreDto.toMangaByGenre(): MangaGenreResults.MangaByGenre =
    MangaGenreResults.MangaByGenre(
        id = id,
        title = title,
        url = url,
        imageUrl = images.jpg.image_url,
        synopsis = synopsis,
        favorites = favorites ?: 0
    )

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
    val favorites: Int
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