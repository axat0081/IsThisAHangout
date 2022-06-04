package com.example.isthisahangout.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.isthisahangout.CHANNEL_ID
import com.example.isthisahangout.R
import com.example.isthisahangout.models.Song

class MusicService : Service() {

    private lateinit var song: Song
    private lateinit var mediaSession: MediaSessionCompat
    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(this, "media_session")
        return MyBinder()
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService = this@MusicService
    }

    fun showNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.title)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.play, "Play", null)
            .addAction(R.drawable.pause, "Pause", null)
            .build()
        startForeground(13, notification)
    }
}