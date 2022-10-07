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
    moviesRepository: MoviesRepository
) : ViewModel() {

    var refreshInProgress = false
    var pendingScrollToTop = false

    val movies = moviesRepository.getMoviesPaged().cachedIn(viewModelScope)

}