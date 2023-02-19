package com.example.isthisahangout.models.music

enum class PlaybackState { IDLE, BUFFERING, READY, ENDED }

data class MusicState(
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val playWhenReady: Boolean = false,
    val duration: Long = 1L,
)