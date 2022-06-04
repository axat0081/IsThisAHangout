package com.example.isthisahangout.service.uploadService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.isthisahangout.R
import javax.inject.Singleton

@Singleton
abstract class BaseService : Service() {

    companion object {
        private const val CHANNEL_ID = "FirebaseTask"
        internal const val PROGRESS_NOTIFICATION_ID = 0
        internal const val FINISHED_NOTIFICATION_ID = 1
    }

    private var numTasks = 0

    private val manager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun taskStarted() {
        changeNumberOfTasks(1)
    }

    fun taskCompleted() {
        changeNumberOfTasks(-1)
    }

    @Synchronized
    private fun changeNumberOfTasks(delta: Int) {
        numTasks += delta
        if (numTasks <= 0) {
            stopSelf()
        }
    }

    private fun createDefaultChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Default",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    protected fun showProgressNotification(
        caption: String,
        completedUnits: Long,
        totalUnits: Long,
        indeterminate:Boolean
    ) {
        var percentComplete = 0
        if (totalUnits > 0) {
            percentComplete = (100 * completedUnits / totalUnits).toInt()
        }

        createDefaultChannel()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.updating)
            .setContentTitle("Uploading file")
            .setContentText(caption)
            .setSound(null)
            .setColor(resources.getColor(R.color.mypink))
            .setProgress(100, percentComplete, indeterminate)
            .setOngoing(true)
            .setAutoCancel(false)

        manager.notify(PROGRESS_NOTIFICATION_ID, builder.build())
    }

    protected fun showFinishedNotification(caption: String, intent: Intent, success: Boolean) {
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* requestCode */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val icon = if (success) R.drawable.success else R.drawable.error

        createDefaultChannel()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(caption)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        manager.notify(FINISHED_NOTIFICATION_ID, builder.build())
    }

    protected fun dismissProgressNotification() {
        manager.cancel(PROGRESS_NOTIFICATION_ID)
    }
}