package com.example.isthisahangout.service.music

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.common.Player.*
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import androidx.work.await
import com.example.isthisahangout.models.Song
import com.example.isthisahangout.models.asSong
import com.example.isthisahangout.models.music.MusicState
import com.example.isthisahangout.models.toMediaItem
import com.example.isthisahangout.utils.asPlaybackState
import com.example.isthisahangout.utils.orDefaultTimestamp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicServiceConnection @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var mediaBrowser: MediaBrowser? = null

    private var songList = emptyList<Song>()

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    val currentPosition = flow {
        while (currentCoroutineContext().isActive) {
            val currentPosition = mediaBrowser?.currentPosition ?: 0L
            emit(currentPosition)
            delay(1L)
        }
    }

    init {
        coroutineScope.launch {
            mediaBrowser = MediaBrowser.Builder(
                context,
                SessionToken(context, ComponentName(context, MusicService::class.java))
            ).buildAsync().await().apply {
                addListener(PlayerListener())
                setMediaItems(songList.map {
                    it.toMediaItem()
                }, 0, 0L)
            }
        }
    }

    fun skipPrevious() = mediaBrowser?.run {
        seekToPrevious()
        play()
    }

    fun play() = mediaBrowser?.play()
    fun pause() = mediaBrowser?.pause()

    fun skipNext() = mediaBrowser?.run {
        seekToNext()
        play()
    }

    fun skipTo(position: Long) = mediaBrowser?.run {
        seekTo(position)
        play()
    }

    fun setSongList(songs: List<Song>) {
        songList = songs
    }

    fun playSong(
        song: Song,
    ) {
        Log.e("songList", songList.toString())
        val index = songList.indexOf(song)
        mediaBrowser?.run {
            seekTo(index, 0L)
            prepare()
            play()
        }
    }

    private inner class PlayerListener : Listener {
        override fun onEvents(player: Player, events: Events) {
            if (events.containsAny(
                    EVENT_PLAYBACK_STATE_CHANGED,
                    EVENT_MEDIA_METADATA_CHANGED,
                    EVENT_PLAY_WHEN_READY_CHANGED
                )
            ) {
                updateMusicState(player)
            }
        }
    }

    private fun updateMusicState(player: Player) {
        Log.e("currentSong", player.currentMediaItem.toString())
        _musicState.value = MusicState(
            currentSong = player.currentMediaItem?.asSong() ?: Song(
                id = "",
                title = "",
                text = "",
                url = "",
                username = "",
                pfp = "",
                time = 0,
                thumbnail = ""
            ),
            playbackState = player.playbackState.asPlaybackState(),
            playWhenReady = player.playWhenReady,
            duration = player.duration.orDefaultTimestamp()
        )
    }

    fun release() {
        mediaBrowser?.run {
            release()
        }
    }
}