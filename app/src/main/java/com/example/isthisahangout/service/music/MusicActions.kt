package com.example.isthisahangout.service.music

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.example.isthisahangout.R
import com.example.isthisahangout.models.music.MusicAction
import com.example.isthisahangout.models.music.asNotificationAction
import com.example.isthisahangout.utils.AppIcons
import com.google.common.collect.ImmutableList

internal object MusicActions {
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    internal fun getRepeatShuffleAction(
        mediaSession: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory
    ) = actionFactory.createCustomActionFromCustomCommandButton(mediaSession, customLayout.first())

    internal fun getSkipPreviousAction(
        context: Context,
        mediaSession: MediaSession,
        actionFactory: MediaNotification.ActionFactory
    ) = MusicAction(
        iconResource = R.drawable.ic_skip_previous,
        titleResource = R.string.previous,
        command = Player.COMMAND_SEEK_TO_PREVIOUS
    ).asNotificationAction(context, mediaSession, actionFactory)

    internal fun getPlayPauseAction(
        context: Context,
        mediaSession: MediaSession,
        actionFactory: MediaNotification.ActionFactory,
        playWhenReady: Boolean
    ) = MusicAction(
        iconResource = if (playWhenReady) AppIcons.Pause.resourceId else AppIcons.Play.resourceId,
        titleResource = if (playWhenReady) R.string.pause else R.string.play,
        command = Player.COMMAND_PLAY_PAUSE
    ).asNotificationAction(context, mediaSession, actionFactory)

    internal fun getSkipNextAction(
        context: Context,
        mediaSession: MediaSession,
        actionFactory: MediaNotification.ActionFactory
    ) = MusicAction(
        iconResource = AppIcons.SkipNext.resourceId,
        titleResource = R.string.skip_next,
        command = Player.COMMAND_SEEK_TO_NEXT
    ).asNotificationAction(context, mediaSession, actionFactory)
}