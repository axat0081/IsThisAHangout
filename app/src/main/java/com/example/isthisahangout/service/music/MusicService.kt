package com.example.isthisahangout.service.music

import androidx.media3.common.Player
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MusicService : MediaLibraryService() {

    private var mediaLibrarySession: MediaLibrarySession? = null

    @Inject
    lateinit var musicNotificationProvider: MusicNotificationProvider

    @Inject
    lateinit var musicSessionCallback: MusicSessionCallback


    @Inject
    @Named("MusicPlayer")
    lateinit var musicPlayer: Player

    override fun onCreate() {
        super.onCreate()
        mediaLibrarySession =
            MediaLibrarySession.Builder(this, musicPlayer, musicSessionCallback).build()
        setMediaNotificationProvider(musicNotificationProvider)
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