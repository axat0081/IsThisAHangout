package com.example.isthisahangout.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class MangaDetailResponse(
    val data: MangaDetailDto
)

data class MangaDetailDto(
    @SerializedName("mal_id")
    val id: Int,
    val url: String,
    val title: String,
    val rating: String?,
    val synopsis: String?,
    val images: AnimeDetailDto.Images,
    val genres: List<AnimeDetailDto.Genres>,
    val favorites: Int?
)

@Entity(tableName = "manga_detail")
data class MangaDetail(
    @PrimaryKey
    val id: Int,
    val url: String,
    val title: String,
    val rating: String?,
    val image: String,
    val genres: String,
    val favorites: Int,
    val synopsis: String
)

fun MangaDetailDto.toMangaDetail(): MangaDetail {
    var gen = ""
    for (g in genres) {
        gen += g.name
        gen += ","
    }
    return MangaDetail(
        id = id,
        url = url,
        title = title,
        rating = rating,
        image = images.jpg.image,
        genres = gen,
        favorites = favorites ?: 0,
        synopsis = synopsis ?: "NA"
    )
}