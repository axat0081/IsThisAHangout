package com.example.isthisahangout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.isthisahangout.repository.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MoviesViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val moviesRefreshChannel = Channel<RefreshType>()
    private val moviesRefreshFlow = moviesRefreshChannel.receiveAsFlow()

    val movies = moviesRefreshFlow.flatMapLatest { refreshType ->
        moviesRepository.getMoviesPaged(forceRefresh = refreshType == RefreshType.FORCE_REFRESH)
    }.cachedIn(viewModelScope)

    fun onForceRefresh() {
        viewModelScope.launch {
            moviesRefreshChannel.send(RefreshType.FORCE_REFRESH)
        }
    }

    fun onStart() {
        viewModelScope.launch {
            moviesRefreshChannel.send(RefreshType.NORMAL_REFRESH)
        }
    }

    enum class RefreshType {
        NORMAL_REFRESH,
        FORCE_REFRESH
    }
}