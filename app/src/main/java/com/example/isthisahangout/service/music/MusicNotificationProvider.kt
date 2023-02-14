package com.example.isthisahangout.service.music

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper.MediaStyle
import com.example.isthisahangout.R
import com.example.isthisahangout.SONG_CHANNEL_ID
import com.example.isthisahangout.utils.asArtworkBitmap
import com.google.common.collect.ImmutableList
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import javax.inject.Inject

private const val MusicNotificationId = 1001

@UnstableApi
class MusicNotificationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : MediaNotification.Provider {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun createNotification(
        mediaSession: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback,
    ): MediaNotification {
        val player = mediaSession.player
        val mediaMetaData = player.mediaMetadata
        val builder = NotificationCompat.Builder(context, SONG_CHANNEL_ID)
            .setContentTitle(mediaMetaData.title)
            .setContentText(mediaMetaData.artist)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(MediaStyle(mediaSession))
        builder.addAction(
            MusicActions.getRepeatShuffleAction(
                mediaSession,
                customLayout,
                actionFactory
            )
        )
        builder.addAction(MusicActions.getSkipPreviousAction(context, mediaSession, actionFactory))
        builder.addAction(
            MusicActions.getPlayPauseAction(
                context,
                mediaSession,
                actionFactory,
                player.playWhenReady
            )
        )
        builder.addAction(MusicActions.getSkipNextAction(context, mediaSession, actionFactory))
        setupArtwork(
            uri = mediaMetaData.artworkUri,
            setLargeIcon = builder::setLargeIcon,
            updateNotification = {
                val notification = MediaNotification(MusicNotificationId, builder.build())
                onNotificationChangedCallback.onNotificationChanged(notification)
            }
        )

        return MediaNotification(MusicNotificationId, builder.build())
    }

    private fun setupArtwork(
        uri: Uri?,
        setLargeIcon: (Bitmap?) -> Unit,
        updateNotification: () -> Unit,
    ) = coroutineScope.launch {
        val bitmap = loadArtworkBitmap(uri)
        setLargeIcon(bitmap)
        updateNotification()
    }

    private suspend fun loadArtworkBitmap(uri: Uri?) =
        withContext(IO) { uri?.asArtworkBitmap(context) }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: Bundle,
    ): Boolean = false

    fun cancelCoroutineScope() = coroutineScope.cancel()

}