package com.example.isthisahangout.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayVideoViewModel @Inject constructor() : ViewModel() {
    var simpleExoPlayer: SimpleExoPlayer? = null
}