package com.example.isthisahangout.models.music

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.media3.common.Player
import android.content.Context
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.example.isthisahangout.R
import com.example.isthisahangout.utils.AppIcons
import com.google.common.collect.ImmutableList

data class MusicAction(
    @DrawableRes val iconResource: Int,
    @StringRes val titleResource: Int,
    @Player.Command val command: Int
)

@UnstableApi
internal fun MusicAction.asNotificationAction(
    context: Context,
    mediaSession: MediaSession,
    actionFactory: MediaNotification.ActionFactory
) = actionFactory.createMediaAction(
    mediaSession,
    IconCompat.createWithResource(context, iconResource),
    context.getString(titleResource),
    command
)

@UnstableApi
fun getRepeatShuffleAction(
    mediaSession: MediaSession,
    customLayout: ImmutableList<CommandButton>,
    actionFactory: MediaNotification.ActionFactory
) = actionFactory.createCustomActionFromCustomCommandButton(mediaSession, customLayout.first())

@UnstableApi
fun getSkipPreviousAction(
    context: Context,
    mediaSession: MediaSession,
    actionFactory: MediaNotification.ActionFactory
) = MusicAction(
    iconResource = AppIcons.SkipPrevious.resourceId,
    titleResource = R.string.skip_previous,
    command = Player.COMMAND_SEEK_TO_PREVIOUS
).asNotificationAction(context, mediaSession, actionFactory)

@UnstableApi
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

@UnstableApi
internal fun getSkipNextAction(
    context: Context,
    mediaSession: MediaSession,
    actionFactory: MediaNotification.ActionFactory
) = MusicAction(
    iconResource = AppIcons.SkipNext.resourceId,
    titleResource = R.string.skip_next,
    command = Player.COMMAND_SEEK_TO_NEXT
).asNotificationAction(context, mediaSession, actionFactory)