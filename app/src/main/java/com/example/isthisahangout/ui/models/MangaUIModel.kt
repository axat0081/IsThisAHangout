package com.example.isthisahangout.ui.models

sealed class MangaUIModel {
    data class MangaModel(
        val id: String,
        val title: String,
        val imageUrl: String,
        val favorites: Int,
        val isHidden: Boolean,
        val isFav: Boolean
    ) : MangaUIModel()

    data class MangaSeparator(val favorites: Int) : MangaUIModel()
}