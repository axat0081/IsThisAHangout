package com.example.isthisahangout.models.music

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession

data class MusicAction(
    @DrawableRes val iconResource: Int,
    @StringRes val titleResource: Int,
    @Player.Command val command: Int,
)

object MusicCommands {
    const val REPEAT_SHUFFLE = "repeat_shuffle"
    const val REPEAT = "repeat"
    const val REPEAT_ONE = "repeat_one"
    const val SHUFFLE = "shuffle"
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun MusicAction.asNotificationAction(
    context: Context,
    mediaSession: MediaSession,
    actionFactory: MediaNotification.ActionFactory,
) = actionFactory.createMediaAction(
    mediaSession,
    IconCompat.createWithResource(context, iconResource),
    context.getString(titleResource),
    command
)