package com.example.isthisahangout.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.adapter.chat.ChatAdapter
import com.example.isthisahangout.models.FirebaseMessage
import com.example.isthisahangout.utils.messagesQuery
import com.example.isthisahangout.utils.newMessagesQuery
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val state: SavedStateHandle,
    @Named("MessagesRef") private val messagesRef: CollectionReference,
) : ViewModel() {

    private val messageChannel = Channel<MessagingEvent>()
    val messageEventFlow = messageChannel.receiveAsFlow()
    var text = state.get<String>("message_text")
        set(value) {
            field = value
            state["message_text"] = text
        }
    val chatAdapter = ChatAdapter(
        paginationQuery = messagesQuery,
        realTimeQuery = newMessagesQuery,
        parser = {
            it.toObject(FirebaseMessage::class.java)
        },
        prefetchDistance = 3,
        pageSize = 10
    )

    fun onStart() {
        chatAdapter.onStart()
    }

    fun onDestroy() {
        chatAdapter.onDestroy()
    }

    fun onSendClick() {
        if (text?.isNotEmpty() == true) {
            viewModelScope.launch {
                val docRef = messagesRef.document()
                docRef.set(
                    FirebaseMessage(
                        senderId = MainActivity.userId,
                        text = text,
                        id = docRef.id,
                        username = MainActivity.userName
                    )
                ).addOnFailureListener {
                    viewModelScope.launch {
                        messageChannel.send(
                            MessagingEvent.MessagesSendError(
                                it.localizedMessage ?: "Network error, check internet connection"
                            )
                        )
                    }
                }
            }
        }
    }

    sealed class MessagingEvent {
        data class MessagesSendError(val message: String) : MessagingEvent()
    }
}