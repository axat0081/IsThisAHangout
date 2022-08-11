package com.example.isthisahangout.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AnimeDetailDto(
    @SerializedName("mal_id")
    val id: Int,
    val url: String,
    val title: String,
    val rating: String?,
    val synopsis: String?,
    val images: Images,
    val genres: List<Genres>
) {
    data class Images(
        val jpg: Jpg
    ) {
        data class Jpg(
            @SerializedName("image_url")
            val image: String
        )
    }

    data class Genres(
        val name: String
    )
}

