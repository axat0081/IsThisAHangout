package com.example.isthisahangout.viewmodel.detailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.cache.favourites.FavouritesDao
import com.example.isthisahangout.models.favourites.FavManga
import com.example.isthisahangout.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MANGA_ID = "mangaId"
const val MANGA_NAME = "mangaName"

@HiltViewModel
class MangaDetailViewModel @Inject constructor(
    mangaRepository: MangaRepository,
    savedStateHandle: SavedStateHandle,
    private val favouritesDao: FavouritesDao
) : ViewModel() {

    private val mangaId = savedStateHandle.get<String>(MANGA_ID) ?: "1"
    private val mangaName = savedStateHandle.get<String>(MANGA_NAME) ?: "Dragonball"

    private val mangaBookMarkEventChannel = Channel<MangaBookMarkEvent>()
    val mangaBookMarkEventFlow = mangaBookMarkEventChannel.receiveAsFlow()

    val isFav = favouritesDao.getManga(userId = MainActivity.userId, "")
        .map { favManga ->
            favManga.any { it.id == mangaId }
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily, false
        )

    val mangaDetail = mangaRepository.getMangaDetail(mangaId).stateIn(
        viewModelScope,
        SharingStarted.Lazily, null
    )

    fun onBookMarkClick() {
        if (isFav.value) {
            viewModelScope.launch {
                favouritesDao.deleteManga(mangaId, MainActivity.userId)
                mangaBookMarkEventChannel.send(MangaBookMarkEvent.RemovedFromBookMarks)
            }
        } else {
            viewModelScope.launch {
                val manga = mangaDetail.value?.data ?: return@launch
                favouritesDao.insertManga(
                    FavManga(
                        id = mangaId,
                        userId = MainActivity.userId,
                        title = mangaName,
                        image = manga.image
                    )
                )
                mangaBookMarkEventChannel.send(MangaBookMarkEvent.AddedToBookMarks)
            }
        }
    }

    sealed class MangaBookMarkEvent {
        object RemovedFromBookMarks : MangaBookMarkEvent()
        object AddedToBookMarks : MangaBookMarkEvent()
    }

}