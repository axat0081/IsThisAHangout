package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
data class FirebasePost(
    val id: String? = null,
    val title: String? = null,
    val username: String? = null,
    val pfp: String? = null,
    val time: Long? = null,
    val text: String? = null,
    val image: String? = null,
    var likes: Int? = null,
) : Parcelable

@Entity(tableName = "liked_posts_ids")
data class LikedPostId(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val postId: String,
    val userId:String
)

@Parcelize
data class Comments(
    val username: String? = null,
    val contentId: String? = null,
    val pfp: String? = null,
    val text: String? = null,
    val time: Long? = null,
    val image: String? = null
) : Parcelable
