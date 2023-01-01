package com.example.isthisahangout.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.isthisahangout.repository.AnimeRepository
import com.example.isthisahangout.usecases.anime.GetAiringAnimeUseCase
import com.example.isthisahangout.usecases.anime.GetUpcomingAnimeUseCase
import com.example.isthisahangout.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeViewModel @Inject constructor(
    getUpcomingAnimeUseCase: GetUpcomingAnimeUseCase,
    getAiringAnimeUseCase: GetAiringAnimeUseCase,
    animeRepository: AnimeRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val quoteRefreshTrigger = Channel<Refresh>()
    private val quoteRefresh = quoteRefreshTrigger.receiveAsFlow()
    private val quoteEventChannel = Channel<QuoteEvent>()
    val quoteEventFlow = quoteEventChannel.receiveAsFlow()
    var quotePendingScrollToTopAfterRefresh = false
    private val animeName = MutableStateFlow("dragon ball")
    private val animeDayQuery = MutableStateFlow("Sunday")
    var animeNameText = state.get<String>("anime_name") ?: ""
        set(value) {
            field = value
            state["anime_name"] = animeNameText
        }

    val airingAnime = getAiringAnimeUseCase().cachedIn(viewModelScope)
    val upcomingAnime = getUpcomingAnimeUseCase().cachedIn(viewModelScope)


    val animeQuotes = quoteRefresh.flatMapLatest { refresh ->
        animeRepository.getAnimeQuote(
            forceRefresh = refresh == Refresh.FORCE,
            onFetchSuccess = {
                quotePendingScrollToTopAfterRefresh = true
            },
            onFetchFailed = { t ->
                viewModelScope.launch { quoteEventChannel.send(QuoteEvent.ShowErrorMessage(t)) }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val animeByName = animeName.flatMapLatest { query ->
        animeRepository.getAnimeByName(query)
    }

    val animeByDay = animeDayQuery.flatMapLatest { query ->
        animeRepository.getAnimeByDay(query)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val animePics = animeRepository.getAnimePics()

    val animeNews = animeRepository.getAnimeNews()

    fun onQuoteStart() {
        if (animeQuotes.value !is Resource.Loading) {
            viewModelScope.launch {
                quoteRefreshTrigger.send(Refresh.NORMAL)
            }
        }
    }

    fun onQuoteManualRefresh() {
        if (animeQuotes.value !is Resource.Loading) {
            viewModelScope.launch {
                quoteRefreshTrigger.send(Refresh.FORCE)
            }
        }
    }


    fun searchAnimeByNameClick() {
        animeName.value = animeNameText
    }


    fun searchAnimeByDay(query: String) {
        animeDayQuery.value = query
    }

    enum class Refresh {
        FORCE, NORMAL
    }

    sealed class QuoteEvent {
        data class ShowErrorMessage(val error: Throwable) : QuoteEvent()
    }

}