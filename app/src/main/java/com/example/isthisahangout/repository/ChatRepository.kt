package com.example.isthisahangout.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.isthisahangout.models.FirebaseMessage
import com.example.isthisahangout.pagingsource.MessagesPagingSource
import com.example.isthisahangout.utils.asFlow
import com.example.isthisahangout.utils.chatCollectionReference
import com.example.isthisahangout.utils.messagesQuery
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val MESSAGES_PAGE_SIZE = 20

@Singleton
class ChatRepository @Inject constructor(

) {

    fun getMessagesPaged(): Flow<PagingData<FirebaseMessage>> =
        Pager(
            config = PagingConfig(
                pageSize = MESSAGES_PAGE_SIZE
            ),
            pagingSourceFactory = { MessagesPagingSource() }
        ).flow

    suspend fun loadMessages(lastLoadedMessage: FirebaseMessage?): List<FirebaseMessage> {
        return if (lastLoadedMessage == null) {
            chatCollectionReference.orderBy("time", Query.Direction.DESCENDING)
                .limit(20)
                .get().await().map {
                    it.toObject(FirebaseMessage::class.java)
                }
        } else {
            chatCollectionReference.orderBy("time", Query.Direction.DESCENDING)
                .startAfter(lastLoadedMessage)
                .limit(20)
                .get().await().map {
                    it.toObject(FirebaseMessage::class.java)
                }
        }
    }

    fun getNewMessages(): Flow<List<FirebaseMessage>> =
        chatCollectionReference.orderBy("time", Query.Direction.DESCENDING)
            .whereGreaterThan("time", Timestamp.now())
            .asFlow()
            .map {
                it.toObjects(FirebaseMessage::class.java)
            }
}