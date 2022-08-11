package com.example.isthisahangout.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.isthisahangout.repository.AnimeRepository
import com.example.isthisahangout.usecases.anime.GetAiringAnimeUseCase
import com.example.isthisahangout.usecases.anime.GetAnimeByGenreUseCase
import com.example.isthisahangout.usecases.anime.GetUpcomingAnimeUseCase
import com.example.isthisahangout.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeViewModel @Inject constructor(
    getAnimeByGenreUseCase: GetAnimeByGenreUseCase,
    getUpcomingAnimeUseCase: GetUpcomingAnimeUseCase,
    getAiringAnimeUseCase: GetAiringAnimeUseCase,
    animeRepository: AnimeRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    private val genreQuery = MutableLiveData("1")
    private val genreQueryFlow = genreQuery.asFlow()
    private val queryMap = HashMap<String, String>()
    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()
    private val quoteRefreshTrigger = Channel<Refresh>()
    private val quoteRefresh = quoteRefreshTrigger.receiveAsFlow()
    private val eventChannel = Channel<Event>()
    val seasonEvents = eventChannel.receiveAsFlow()
    private val quoteEventChannel = Channel<QuoteEvent>()
    val quoteEventFlow = quoteEventChannel.receiveAsFlow()
    val year = MutableStateFlow("2020")
    val season = MutableStateFlow("summer")
    var pendingScrollToTopAfterRefresh = false
    var quotePendingScrollToTopAfterRefresh = false
    private val animeName = MutableStateFlow("dragon ball")
    private val animeDayQuery = MutableStateFlow("Sunday")
    var animeNameText = state.get<String>("anime_name") ?: ""
        set(value) {
            field = value
            state["anime_name"] = animeNameText
        }

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

    val airingAnime = getAiringAnimeUseCase().cachedIn(viewModelScope)
    val upcomingAnime = getUpcomingAnimeUseCase().cachedIn(viewModelScope)
    val animeByGenre = genreQueryFlow.flatMapLatest {
        getAnimeByGenreUseCase(it)
    }.cachedIn(viewModelScope)

    val animeBySeason = combine(
        season, year, refreshTrigger
    ) { season, year, refreshTrigger ->
        Triple(season, year, refreshTrigger)
    }.flatMapLatest { (season, year, refresh) ->
        animeRepository.getAnimeBySeason(
            season = season,
            year = year,
            forceRefresh = refresh == Refresh.FORCE,
            onFetchSuccess = {
                pendingScrollToTopAfterRefresh = true
            },
            onFetchFailed = { t ->
                viewModelScope.launch { eventChannel.send(Event.ShowErrorMessage(t)) }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

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

    fun onStart() {
        if (animeBySeason.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.NORMAL)
            }
        }
    }

    fun onQuoteStart() {
        if (animeQuotes.value !is Resource.Loading) {
            viewModelScope.launch {
                quoteRefreshTrigger.send(Refresh.NORMAL)
            }
        }
    }

    fun onManualRefresh() {
        if (animeBySeason.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.FORCE)
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

    fun searchAnimeByGenre(query: String) {
        if (queryMap.containsKey(query))
            genreQuery.value = queryMap[query]
        else genreQuery.value = "1"
    }

    fun searchAnimeBySeason(query: String) {
        season.value = query
    }

    fun searchAnimeByNameClick() {
        animeName.value = animeNameText
    }

    fun searchAnimeByYear(query: String) {
        year.value = query
    }

    fun searchAnimeByDay(query: String) {
        animeDayQuery.value = query
    }

    enum class Refresh {
        FORCE, NORMAL
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }

    sealed class QuoteEvent {
        data class ShowErrorMessage(val error: Throwable) : QuoteEvent()
    }
}