package com.example.isthisahangout.ui.models

sealed class AnimeUIModel {
    data class AnimeModel(
        val id: String,
        val title: String,
        val imageUrl: String,
        val startDate: String?,
        val isHidden: Boolean,
        val isFav: Boolean
    ) : AnimeUIModel()

    data class AnimeSeparator(val desc: String) : AnimeUIModel()
}
