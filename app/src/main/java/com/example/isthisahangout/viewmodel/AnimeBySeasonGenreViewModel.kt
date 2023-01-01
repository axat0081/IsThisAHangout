package com.example.isthisahangout.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.isthisahangout.usecases.anime.GetAnimeByGenreUseCase
import com.example.isthisahangout.usecases.anime.GetAnimeBySeasonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class AnimeBySeasonGenreViewModel @Inject constructor(
    getAnimeByGenreUseCase: GetAnimeByGenreUseCase,
    getAnimeBySeasonsUseCase: GetAnimeBySeasonsUseCase
) : ViewModel() {

    private val genreQuery = MutableLiveData("1")
    private val genreQueryFlow = genreQuery.asFlow()
    private val queryMap = HashMap<String, String>()
    val year = MutableStateFlow("2020")
    val season = MutableStateFlow("summer")

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

    var animeByGenreRefreshInProgress = false
    var animeBySeasonRefreshInProgress = false
    var animeByGenrePendingScrollToTop = false
    var animeBySeasonPendingScrollToTop = false

    val animeByGenre = genreQueryFlow.flatMapLatest {
        getAnimeByGenreUseCase(it)
    }.cachedIn(viewModelScope)

    val animeBySeason = combine(season, year) { season, year ->
        Pair(season, year)
    }.flatMapLatest { (season, year) ->
        getAnimeBySeasonsUseCase.invoke(season = season, year = year).cachedIn(viewModelScope)
    }

    fun searchAnimeByGenre(query: String) {
        if (queryMap.containsKey(query))
            genreQuery.value = queryMap[query]
        else genreQuery.value = "1"
    }

    fun searchAnimeByYear(query: String) {
        year.value = query
    }

    fun searchAnimeBySeason(query: String) {
        season.value = query
    }
}