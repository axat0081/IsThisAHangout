package com.example.isthisahangout.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.ComfortCharacter
import com.example.isthisahangout.models.FirebasePost
import com.example.isthisahangout.models.reminders.Reminder
import com.example.isthisahangout.service.reminder.ReminderReceiver
import com.example.isthisahangout.utils.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext
    val context: Context,
    @Named("ComfortCharacterRef")
    private val comfortCharacterRef: CollectionReference,
    @Named("RemindersRef")
    private val remindersRef: CollectionReference,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    fun getComfortCharacters(userId: String): Flow<List<ComfortCharacter>> =
        comfortCharacterRef.document(userId).collection("comfort_characters")
            .orderBy("priority")
            .asFlow().map { snapshots ->
                snapshots.map {
                    it.toObject(ComfortCharacter::class.java)
                }
            }

    fun getReminders(userId: String): Flow<Resource<List<Reminder>>> =
        remindersRef.document(userId).collection("reminders")
            .orderBy("time")
            .asResourceFlow { snapshots ->
                snapshots.toObjects(Reminder::class.java)
            }

    @SuppressLint("MissingPermission")
    fun createReminder(userId: String, reminder: Reminder): Flow<Resource<FirebaseResult>> = flow {
        emit(Resource.Loading())
        val id = userId + System.currentTimeMillis()
        val newReminder = reminder.copy(id = id)
        try {
            remindersRef.document(userId).collection("reminders")
                .document(id)
                .set(newReminder)
                .await()
            val intent = Intent(context, ReminderReceiver::class.java)
                .apply {
                    putExtra("reminder_name", reminder.name)
                    putExtra("reminder_desc", reminder.desc)
                    putExtra("reminder_user_id", reminder.userId)
                    putExtra("reminder_done",reminder.done)
                }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.time,
                PendingIntent.getBroadcast(
                    context,
                    reminder.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            emit(Resource.Success(FirebaseResult(result = true, message = "Reminder added")))
        } catch (exception: Exception) {
            emit(Resource.Error(exception))
        }
    }

    suspend fun sendTokenToServer(token: String) {
        FirebaseFirestore.getInstance().collection("Tokens")
            .document(MainActivity.userId).set(token).await()
    }

    fun getUserPosts(
        userId: String,
    ): Flow<Resource<List<FirebasePost>>> = flow {
        emit(Resource.Loading())
        try {
            val posts =
                postsQuery.whereEqualTo("userId", userId)
                    .orderBy("time")
                    .get()
                    .await()
                    .toObjects(FirebasePost::class.java)
            emit(Resource.Success(data = posts))
        } catch (exception: HttpException) {
            emit(Resource.Error(throwable = exception))
        } catch (exception: IOException) {
            emit(Resource.Error(throwable = exception))
        }
    }

    suspend fun updateReminder(userId: String, reminderId: String, isChecked: Boolean) {
        remindersRef.document(userId).collection("reminders").document(reminderId)
            .update("done", isChecked)
            .await()
    }

}