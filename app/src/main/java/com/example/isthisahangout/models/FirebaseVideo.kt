package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseVideo(
    val id: String? = null,
    val url: String? = null,
    val title: String? = null,
    val pfp: String? = null,
    val userId: String? = null,
    val username: String? = null,
    val time: Long? = null,
    val text: String? = null,
    val image: String? = null,
    var likes: Int? = null,
    val thumbnail: String? = null
):Parcelable

@Entity(tableName = "liked_videos_ids")
data class LikedVideoId(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: String,
    val userId: String
)
