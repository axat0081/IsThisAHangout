package com.example.isthisahangout.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.favourites.FavAnime
import com.example.isthisahangout.models.favourites.FavGame
import com.example.isthisahangout.models.favourites.FavPost
import com.example.isthisahangout.cache.favourites.FavouritesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val favDoa: FavouritesDao,
    state: SavedStateHandle
) : ViewModel() {

    val isAnimeBookMarked = MutableStateFlow(false)
    val favAnimeQuery = state.getLiveData("AnimeQuery", "")
    val favGameQuery = state.getLiveData("GameQuery", "")
    val favPostQuery = state.getLiveData("PostQuery", "")
    val favVideoQuery = state.getLiveData("VideoQuery", "")

    val favAnime = favAnimeQuery.asFlow().flatMapLatest {
        favDoa.getAnime(searchQuery = it, userId = MainActivity.userId)
    }

    val favGame = favGameQuery.asFlow().flatMapLatest {
        favDoa.getGames(searchQuery = it, userId = MainActivity.userId)
    }

    val favPost = favPostQuery.asFlow().flatMapLatest {
        favDoa.getPosts(searchQuery = it, userId = MainActivity.userId)
    }

    val favVideo = favVideoQuery.asFlow().flatMapLatest {
        favDoa.getVideos(searchQuery = it, userId = MainActivity.userId)
    }

    fun addAnime(anime: FavAnime) {
        viewModelScope.launch {
            favDoa.insertAnime(anime)
        }
    }

    fun addGame(game: FavGame) {
        viewModelScope.launch {
            favDoa.insertGame(game)
        }
    }

    fun addPost(post: FavPost) {
        viewModelScope.launch {
            favDoa.insertPost(post)
        }
    }

    fun deleteAnime(id: Int) {
        viewModelScope.launch {
            favDoa.deleteAnime(
                id = id,
                userId = MainActivity.userId!!
            )
        }
    }

    fun deleteGame(id: Int) {
        viewModelScope.launch {
            favDoa.deleteGame(
                id = id,
                userId = MainActivity.userId!!
            )
        }
    }

}