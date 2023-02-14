package com.example.isthisahangout.models.music

import com.example.isthisahangout.models.Song

enum class PlaybackState { IDLE, BUFFERING, READY, ENDED }

data class MusicState(
    val currentSong: Song = Song(
        id = "",
        title = "",
        text = "",
        url = "",
        username = "",
        pfp = "",
        time = 0,
        thumbnail = ""
    ),
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val playWhenReady: Boolean = false,
    val duration: Long = 1L,
)