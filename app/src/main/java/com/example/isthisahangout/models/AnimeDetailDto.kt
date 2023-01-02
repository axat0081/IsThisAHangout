package com.example.isthisahangout.models

import com.google.gson.annotations.SerializedName

data class AnimeDetailResponse(
    val data: AnimeDetailDto
)

data class AnimeDetailDto(
    @SerializedName("mal_id")
    val id: Int,
    val url: String,
    val title: String,
    val rating: String?,
    val synopsis: String?,
    val images: Images,
    val genres: List<Genres>,
    val trailer: Trailer?,
    val favorites: Int?,

) {
    data class Images(
        val jpg: Jpg
    ) {
        data class Jpg(
            @SerializedName("image_url")
            val image: String
        )
    }

    data class Trailer(
        @SerializedName("youtube_id")
        val youtubeId: String,
        val url: String
    )

    data class Genres(
        val name: String
    )
}

