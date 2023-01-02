package com.example.isthisahangout.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_detail")
data class AnimeDetail(
    @PrimaryKey
    val id: Int,
    val url: String,
    val title: String,
    val rating: String,
    val image: String,
    val genres: String,
    val synopsis: String,
    val trailerId: String?,
    val trailerUrl: String?,
    val favorites: Int
)

fun AnimeDetailDto.toAnimeDetail(): AnimeDetail {
    var gen = ""
    for (g in genres) {
        gen += g.name
        gen += ","
    }
    gen.removeSuffix(",")
    return AnimeDetail(
        id = id,
        url = url,
        title = title,
        rating = rating ?: "NA",
        image = images.jpg.image,
        genres = gen,
        synopsis = synopsis ?: "NA",
        trailerId = trailer?.youtubeId,
        trailerUrl = trailer?.url,
        favorites = favorites?:0,
    )
}