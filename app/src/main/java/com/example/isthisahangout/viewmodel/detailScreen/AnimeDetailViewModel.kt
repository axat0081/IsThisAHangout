package com.example.isthisahangout.viewmodel.detailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

const val ANIME_DETAIL_ID = "com.example.isthisahangout.viewmodel.detailScreen.anime_detail.id"

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val animeId = savedStateHandle.getStateFlow<String?>(ANIME_DETAIL_ID, null)

    val animeDetail = animeId.flatMapLatest { animeId ->
        animeId?.let { id ->
            animeRepository.getAnimeDetail(id)
        } ?: emptyFlow()
    }.stateIn(viewModelScope, SharingStarted.Lazily,null)

    fun setAnimeId(id: String) {
        savedStateHandle[ANIME_DETAIL_ID] = id
    }
}