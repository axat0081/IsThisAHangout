package com.example.isthisahangout.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AnimePics(
    @SerializedName("files")
    val images: List<String>
)

@Entity(tableName = "anime_pics")
data class AnimeImage(
    @PrimaryKey val image: String
)

data class FieldHolder(
    val exclude: List<String> = emptyList()
)