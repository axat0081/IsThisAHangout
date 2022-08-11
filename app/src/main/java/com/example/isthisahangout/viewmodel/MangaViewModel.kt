package com.example.isthisahangout.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.isthisahangout.usecases.manga.GetMangaByGenreUseCase
import com.example.isthisahangout.usecases.manga.GetMangaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class MangaViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val getMangaUseCase: GetMangaUseCase,
    private val getMangaByGenreUseCase: GetMangaByGenreUseCase
) : ViewModel() {
    private val queryMap = HashMap<String, String>()

    init {
        queryMap["Action"] = "1"
        queryMap["Adventure"] = "2"
        queryMap["Mystery"] = "7"
        queryMap["Fantasy"] = "10"
        queryMap["Comedy"] = "4"
        queryMap["Horror"] = "14"
        queryMap["Magic"] = "16"
        queryMap["Mecha"] = "18"
        queryMap["Romance"] = "22"
        queryMap["Music"] = "19"
        queryMap["Shoujo"] = "25"
        queryMap["Sci Fi"] = "24"
        queryMap["Shounen"] = "27"
        queryMap["Psychological"] = "40"
        queryMap["Slice Of Life"] = "36"
    }

    val manga = getMangaUseCase().cachedIn(viewModelScope)
    private val mangaByGenreQuery = MutableStateFlow("1")
    val mangaByGenre = mangaByGenreQuery.flatMapLatest { genre ->
        getMangaByGenreUseCase(genre)
    }.cachedIn(viewModelScope)

    fun searchMangaByGenre(query: String) {
        if (queryMap.containsKey(query))
            mangaByGenreQuery.value = queryMap[query]!!
        else mangaByGenreQuery.value = "1"
    }
}