package com.example.isthisahangout.repository

import com.example.isthisahangout.models.FirebaseMessage
import com.example.isthisahangout.utils.asFlow
import com.example.isthisahangout.utils.chatCollectionReference
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val MESSAGES_PAGE_SIZE = 10

@Singleton
class ChatRepository @Inject constructor(

) {

    suspend fun loadOldMessages(lastLoadedMessage: FirebaseMessage?): List<FirebaseMessage> {
        val messages = if (lastLoadedMessage == null) {
            chatCollectionReference.orderBy("time", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
        } else {
            chatCollectionReference.orderBy("time", Query.Direction.DESCENDING)
                .startAfter(lastLoadedMessage)
                .limit(10)
                .get()
                .await()
        }
        return messages.toObjects(FirebaseMessage::class.java)
    }

    fun loadNewMessages(): Flow<List<FirebaseMessage>> =
        chatCollectionReference.orderBy("time", Query.Direction.DESCENDING)
            .whereGreaterThan("time", Timestamp.now())
            .asFlow()
            .map {
                it.toObjects(FirebaseMessage::class.java)
            }
}