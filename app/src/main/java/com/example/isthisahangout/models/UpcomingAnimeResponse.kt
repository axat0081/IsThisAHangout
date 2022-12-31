package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.isthisahangout.models.util.Images
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpcomingAnimeResponse(
    val data: List<UpcomingAnimeDto>
) : Parcelable{
    @Parcelize
    @Entity(tableName = "upcoming_anime")
    data class UpcomingAnime(
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
data class UpcomingAnimeDto(
    @PrimaryKey
    @SerializedName("mal_id")
    val id: String,
    val title: String,
    val images: Images,
    val favorites: Int?
) : Parcelable


@Entity(tableName = "upcoming_anime_remote_key")
data class UpcomingAnimeRemoteKey(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)

fun UpcomingAnimeDto.toUpcomingAnime(): UpcomingAnimeResponse.UpcomingAnime =
    UpcomingAnimeResponse.UpcomingAnime(
        id = id,
        title = title,
        imageUrl = images.jpg.image_url,
        favorites = favorites ?: 0
    )