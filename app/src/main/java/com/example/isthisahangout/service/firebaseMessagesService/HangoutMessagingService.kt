package com.example.isthisahangout.service.firebaseMessagesService

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.isthisahangout.GENERIC_CHANNEL_ID
import com.example.isthisahangout.R
import com.example.isthisahangout.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

const val GENERIC_NOTIFICATION_CHANNEL_ID = 4

@AndroidEntryPoint
class HangoutMessagingService constructor(
) : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository
    val scope: CoroutineScope = CoroutineScope(
        SupervisorJob()
    )

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        scope.launch {
            try {
                userRepository.sendTokenToServer(token)
            } catch (exception: Exception) {
                Log.e("tokenError", exception.localizedMessage ?: "An unknown error occurred")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.containsKey("message")) {
            val message = remoteMessage.data["message"]
            val notificationBuilder =
                NotificationCompat.Builder(application, GENERIC_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Hey There : ) ")
                    .setContentText(message)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(
                GENERIC_NOTIFICATION_CHANNEL_ID,
                notificationBuilder.build()
            )
        }
    }
}