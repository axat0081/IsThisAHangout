package com.example.isthisahangout.viewmodel.detailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.cache.favourites.FavouritesDao
import com.example.isthisahangout.models.favourites.FavAnime
import com.example.isthisahangout.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ANIME_DETAIL_ID = "animeId"
const val ANIME_NAME = "animeName"

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    animeRepository: AnimeRepository,
    private val favDoa: FavouritesDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val animeId = savedStateHandle.get<String>(ANIME_DETAIL_ID) ?: "1"
    private val animeTitle = savedStateHandle.get<String>(ANIME_NAME) ?: "Dragon ball"

    val animeDetail =
        animeRepository.getAnimeDetail(animeId).stateIn(viewModelScope, SharingStarted.Lazily, null)

    val isFavAnime = favDoa.getAnime("", MainActivity.userId).map { favAnime ->
        favAnime.any { it.title == animeTitle }
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun onBookMarkClick() {
        if(isFavAnime.value){
            viewModelScope.launch {
                favDoa.deleteAnimeByName(animeTitle,MainActivity.userId)
            }
        } else {
            viewModelScope.launch{
                val anime = animeDetail.value?.data ?: return@launch
                favDoa.insertAnime(FavAnime(userId = MainActivity.userId, title = animeTitle, image = anime.image))
            }
        }
    }
}