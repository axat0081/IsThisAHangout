package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameResults(
    var results: List<Games>
) : Parcelable {
    @Parcelize
    data class Games(
        val id: String,
        val name: String?,
        @SerializedName("background_image")
        val imageUrl: String?,
        val rating: String?,
        val genres: List<Genres>,
        @SerializedName("short_screenshots")
        val picList: ArrayList<ScreenShots>,
    ) : Parcelable {
        @Parcelize
        data class Genres(
            val name: String?
        ) : Parcelable

        @Parcelize
        data class ScreenShots(
            val image: String?
        ) : Parcelable
    }
}

@Entity(tableName = "games")
@Parcelize
data class RoomGames(
    @PrimaryKey val id: String,
    val name: String?,
    val imageUrl: String?,
    val rating: String?,
    val genres: ArrayList<String?>,
    val screenshots: ArrayList<String?>
):Parcelable


@Entity(tableName = "games_remote_key")
data class GameRemoteKey(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
)
