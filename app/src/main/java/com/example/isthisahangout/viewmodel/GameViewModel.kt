package com.example.isthisahangout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.isthisahangout.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    repository: GameRepository
) : ViewModel() {
    val games = repository.getGames().cachedIn(viewModelScope)
}