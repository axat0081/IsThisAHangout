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
import com.example.isthisahangout.SONG_CHANNEL_ID
import com.example.isthisahangout.SONG_NOTIFICATION_ID
import com.example.isthisahangout.models.music.getPlayPauseAction
import com.example.isthisahangout.models.music.getRepeatShuffleAction
import com.example.isthisahangout.models.music.getSkipNextAction
import com.example.isthisahangout.models.music.getSkipPreviousAction
import com.example.isthisahangout.utils.AppIcons
import com.example.isthisahangout.utils.asArtworkBitmap
import com.google.common.collect.ImmutableList
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject


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

        val notificationBuilder = NotificationCompat.Builder(context, SONG_CHANNEL_ID)
            .setContentTitle(mediaSession.player.mediaMetadata.title)
            .setContentText("Uploaded by - ${mediaSession.player.mediaMetadata.artist}")
            .setSmallIcon(AppIcons.Music.resourceId)
            .setStyle(MediaStyle(mediaSession))

        getNotificationActions(
            mediaSession = mediaSession,
            customLayout = customLayout,
            actionFactory = actionFactory,
            playWhenReady = mediaSession.player.playWhenReady
        ).forEach { notificationBuilder.addAction(it) }

        setupArtwork(
            uri = mediaSession.player.mediaMetadata.artworkUri,
            setLargeIcon = notificationBuilder::setLargeIcon,
            updateNotification = {
                val notification =
                    MediaNotification(SONG_NOTIFICATION_ID, notificationBuilder.build())
                onNotificationChangedCallback.onNotificationChanged(notification)
            }
        )

        return MediaNotification(SONG_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getNotificationActions(
        mediaSession: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        playWhenReady: Boolean,
    ) = listOf(
        getRepeatShuffleAction(mediaSession, customLayout, actionFactory),
        getSkipPreviousAction(context, mediaSession, actionFactory),
        getPlayPauseAction(context, mediaSession, actionFactory, playWhenReady),
        getSkipNextAction(context, mediaSession, actionFactory)
    )

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
        withContext(Dispatchers.IO) { uri?.asArtworkBitmap(context) }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: Bundle,
    ): Boolean = false

    fun cancelCoroutineScope() = coroutineScope.cancel()
}