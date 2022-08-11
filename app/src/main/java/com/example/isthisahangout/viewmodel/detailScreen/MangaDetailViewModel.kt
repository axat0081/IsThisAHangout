package com.example.isthisahangout.viewmodel.detailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

const val MANGA_DETAIL_ID = "com.example.isthisahangout.viewmodel.detailScreen.manga_detail_id"

@HiltViewModel
class MangaDetailViewModel @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mangaId = savedStateHandle.getStateFlow<String?>(MANGA_DETAIL_ID, null)

    val mangaDetail = mangaId.flatMapLatest { mangaId ->
        mangaId?.let { id ->
            mangaRepository.getMangaDetail(id)
        } ?: emptyFlow()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun setMangaId(id: String) {
        savedStateHandle[MANGA_DETAIL_ID] = id
    }
}