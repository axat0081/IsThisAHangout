package com.example.isthisahangout.ui.models

sealed class AnimeUIModel {
    data class AnimeModel(
        val id: String,
        val title: String,
        val imageUrl: String,
        val favorites: Int,
        val isHidden: Boolean,
        val isFav: Boolean
    ) : AnimeUIModel()

    data class AnimeSeparator(val favorites: Int) : AnimeUIModel()
}
