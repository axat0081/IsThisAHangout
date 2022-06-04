package com.example.isthisahangout.models.favourites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_favourites")
class FavPost(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val username: String? = null,
    val pfp: String? = null,
    val time: Long? = null,
    val text: String? = null,
    val image: String? = null
)