package com.example.isthisahangout.models.music

import com.example.isthisahangout.models.Song
import com.example.isthisahangout.utils.PlaybackState

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
    val duration: Long = 0L,
)
