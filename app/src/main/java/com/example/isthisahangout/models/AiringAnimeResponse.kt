package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.isthisahangout.models.util.Images
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AiringAnimeResponse(
    val data: List<AiringAnimeDto>
) : Parcelable {
    @Parcelize
    @Entity(tableName = "airing_anime")
    data class AiringAnime(
        @PrimaryKey
        @SerializedName("mal_id")
        val id: String,
        val title: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val favorites: Int
    ) : Parcelable
}

@Parcelize
data class AiringAnimeDto(
    @PrimaryKey
    @SerializedName("mal_id")
    val id: String,
    val title: String,
    val images: Images,
    val favorites: Int?
) : Parcelable



@Entity(tableName = "airing_anime_remote_key")
data class AiringAnimeRemoteKey(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)

fun AiringAnimeDto.toAiringAnime(): AiringAnimeResponse.AiringAnime =
    AiringAnimeResponse.AiringAnime(
        id = id,
        title = title,
        imageUrl = images.jpg.image_url,
        favorites = favorites ?: 0
    )