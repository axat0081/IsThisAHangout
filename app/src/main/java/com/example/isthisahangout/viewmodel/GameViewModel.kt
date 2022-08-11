package com.example.isthisahangout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.isthisahangout.usecases.videogame.GetVideoGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    videoGameUseCase: GetVideoGameUseCase
) : ViewModel() {
    val games = videoGameUseCase().cachedIn(viewModelScope)
}