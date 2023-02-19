package com.example.isthisahangout.service.music

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.Player.*
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import androidx.work.await
import com.example.isthisahangout.models.Song
import com.example.isthisahangout.models.music.MusicState
import com.example.isthisahangout.models.music.PlaybackState
import com.example.isthisahangout.models.toMediaItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicServiceConnection @Inject constructor(
    @ApplicationContext context: Context,
) {
    private var mediaBrowser: MediaBrowser? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    val currentPosition = flow {
        while (currentCoroutineContext().isActive) {
            val currentPosition = mediaBrowser?.currentPosition ?: 0L
            emit(currentPosition)
            delay(POSITION_UPDATE_INTERVAL_MS)
        }
    }

    init {
        coroutineScope.launch {
            mediaBrowser = MediaBrowser.Builder(
                context,
                SessionToken(context, ComponentName(context, MusicService::class.java))
            ).buildAsync().await().apply {
                addListener(PlayerListener())
            }
        }
    }

    fun play() = mediaBrowser?.play()
    fun pause() = mediaBrowser?.pause()
    fun playSong(
        song: Song,
    ) {
        mediaBrowser?.run {
            setMediaItem(song.toMediaItem())
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

        private fun updateMusicState(player: Player) = with(player) {
            _musicState.update {
                it.copy(
                    playbackState = playbackState.asPlaybackState(),
                    playWhenReady = playWhenReady,
                    duration = duration
                )
            }
        }

        private fun Int.asPlaybackState() = when (this) {
            STATE_IDLE -> PlaybackState.IDLE
            STATE_BUFFERING -> PlaybackState.BUFFERING
            STATE_READY -> PlaybackState.READY
            STATE_ENDED -> PlaybackState.ENDED
            else -> PlaybackState.ENDED
        }
    }
}