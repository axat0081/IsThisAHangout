package com.example.isthisahangout

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.twitter.TwitterEmojiProvider
import dagger.hilt.android.HiltAndroidApp

const val SONG_CHANNEL_ID = "song_notification_channel"
const val GENERIC_CHANNEL_ID = "generic_notification_channel"
const val GENERIC_CHANNEL_DESCRIPTION = "Generic Notifications"
const val PLAY = "play"
const val NEXT = "next"
const val PREVIOUS = "previous"
const val EXIT = "EXIT"

@HiltAndroidApp
class HomeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EmojiManager.install(TwitterEmojiProvider())
        Firebase.database.setPersistenceEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val songNotificationChannel =
                NotificationChannel(
                    SONG_CHANNEL_ID,
                    "Song Playing",
                    NotificationManager.IMPORTANCE_HIGH
                )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(songNotificationChannel)
            val genericNotificationChannel =
                NotificationChannel(
                    GENERIC_CHANNEL_ID,
                    "Generic Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                )
            genericNotificationChannel.description = GENERIC_CHANNEL_DESCRIPTION
            notificationManager.createNotificationChannel(genericNotificationChannel)
        }
    }
}