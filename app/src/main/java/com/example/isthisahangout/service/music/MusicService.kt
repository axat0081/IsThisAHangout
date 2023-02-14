package com.example.isthisahangout.service.music

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MusicService : MediaLibraryService() {
    private var mediaLibrarySession: MediaLibrarySession? = null

    @Inject
    lateinit var musicSessionCallback: MusicSessionCallback

    @Inject
    @Named("MusicExoplayer")
    lateinit var musicExoPlayer: ExoPlayer

    @Inject
    lateinit var musicNotificationProvider: MusicNotificationProvider

    override fun onCreate() {
        super.onCreate()
        setMediaNotificationProvider(musicNotificationProvider)
        mediaLibrarySession =
            MediaLibrarySession.Builder(this, musicExoPlayer, musicSessionCallback).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession

    override fun onDestroy() {
        super.onDestroy()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        musicSessionCallback.cancelCoroutineScope()
        musicNotificationProvider.cancelCoroutineScope()
    }
}