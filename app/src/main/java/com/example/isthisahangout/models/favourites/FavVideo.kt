package com.example.isthisahangout.models.favourites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_favourites")
data class FavVideo(
    @PrimaryKey
    val id:String,
    val userId: String,
    val title: String? = null,
    val text: String? = null,
    val url: String? = null,
    val time: Long? = null,
    val username: String? = null,
    val pfp: String? = null,
    val thumbnail: String? = null
)