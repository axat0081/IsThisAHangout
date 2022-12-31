package com.example.isthisahangout.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val PROGRESS_BAR_STATE = "progress_bar_state"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    val showProgressBar = savedStateHandle.getStateFlow(PROGRESS_BAR_STATE, false)

    fun toggleProgressBarState(state: Boolean) {
        savedStateHandle[PROGRESS_BAR_STATE] = state
    }
}