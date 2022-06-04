package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpcomingAnimeResponse(
    val top: List<UpcomingAnime>
) : Parcelable {
    @Parcelize
    @Entity(tableName = "upcoming_anime")
    data class UpcomingAnime(
        @PrimaryKey
        @SerializedName("mal_id")
        val id: String,
        val title: String,
        @SerializedName("image_url")
        val imageUrl: String,
        @SerializedName("start_date")
        val startDate: String?
    ) : Parcelable
}

@Entity(tableName = "upcoming_anime_remote_key")
data class UpcomingAnimeRemoteKey(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)