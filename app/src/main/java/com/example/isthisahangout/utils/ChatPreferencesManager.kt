package com.example.isthisahangout.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.isthisahangout.models.FirebaseMessage
import com.google.firebase.Timestamp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_chat")
    val lastReceivedMessage = context.dataStore.data
        .map { message ->
            FirebaseMessage(
                id = message[MessageKeys.ID],
                text = message[MessageKeys.TEXT],
                senderId = message[MessageKeys.SENDER_ID],
                time = Timestamp(Date(message[MessageKeys.TIME]!!)),
                username = message[MessageKeys.USERNAME]
            )
        } ?: emptyFlow()

    suspend fun updateMessageId(id: String) {
        context.dataStore.edit { profile ->
            profile[ChatPreferencesManager.MessageKeys.ID] = id
        }
    }

    suspend fun updateText(text: String) {
        context.dataStore.edit { profile ->
            profile[ChatPreferencesManager.MessageKeys.TEXT] = text
        }
    }

    suspend fun updateSenderId(senderId: String) {
        context.dataStore.edit { profile ->
            profile[ChatPreferencesManager.MessageKeys.SENDER_ID] = senderId
        }
    }

    suspend fun updateTime(time: Long) {
        context.dataStore.edit { profile ->
            profile[ChatPreferencesManager.MessageKeys.TIME] = time
        }
    }

    suspend fun updateUsername(username: String) {
        context.dataStore.edit { profile ->
            profile[ChatPreferencesManager.MessageKeys.USERNAME] = username
        }
    }


    private object MessageKeys {
        val ID = stringPreferencesKey("message_id")
        val TEXT = stringPreferencesKey("text")
        val SENDER_ID = stringPreferencesKey("sender_id")
        val TIME = longPreferencesKey("time")
        val USERNAME = stringPreferencesKey("username")
    }
}