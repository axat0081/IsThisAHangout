package com.example.isthisahangout.service.music

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.isthisahangout.utils.Constants.MEDIA_NETWORK_ERROR
import com.example.isthisahangout.utils.MusicEvent
import com.example.isthisahangout.utils.MusicResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicServiceConnection(
    context: Context
) {
    private val _isConnected = MutableStateFlow<MusicEvent<MusicResource<Boolean>>?>(null)
    val isConnected = _isConnected.asStateFlow()
    private val _networkError = MutableStateFlow<MusicEvent<MusicResource<Boolean>>?>(null)
    val networkError = _networkError.asStateFlow()
    private val _playbackState = MutableStateFlow<PlaybackStateCompat?>(null)
    val playbackState = _playbackState.asStateFlow()
    private val _currentPlayingSong = MutableStateFlow<MediaMetadataCompat?>(null)
    val currentPlayingSong = _currentPlayingSong.asStateFlow()
    lateinit var mediaController: MediaControllerCompat
    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls
    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply { connect() }

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _isConnected.value = MusicEvent(
                MusicResource.success(true)
            )
        }

        override fun onConnectionSuspended() {
            _isConnected.value = MusicEvent(
                MusicResource.error(
                    "The connection was suspended", false
                )
            )
        }

        override fun onConnectionFailed() {
            _isConnected.value = MusicEvent(
                MusicResource.error(
                    "Couldn't connect to media browser", false
                )
            )
        }
    }

    private inner class MediaControllerCallback :
        MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            _playbackState.value = state
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            _currentPlayingSong.value = metadata
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                MEDIA_NETWORK_ERROR -> {
                    _networkError.value = MusicEvent(
                        MusicResource.error(
                            message = "network error occurred, could not connect to server",
                            data = null
                        )
                    )
                }
            }
        }
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}