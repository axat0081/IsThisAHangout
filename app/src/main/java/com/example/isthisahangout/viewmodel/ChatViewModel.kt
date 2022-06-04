package com.example.isthisahangout.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.adapter.chat.ChatAdapter
import com.example.isthisahangout.models.FirebaseMessage
import com.example.isthisahangout.utils.asFlow
import com.example.isthisahangout.utils.messagesQuery
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val state: SavedStateHandle,
    mAuth: FirebaseAuth,
    @Named("MessagesRef") private val messagesRef: CollectionReference
) : ViewModel() {

    private val messageChannel = Channel<MessagingEvent>()
    val messageEventFlow = messageChannel.receiveAsFlow()
    var text = state.get<String>("message_text")
        set(value) {
            field = value
            state.set("message_text", text)
        }

    init {
        viewModelScope.launch {
            messagesQuery.asFlow().collectLatest { recentMessagesSnapshot->
                val recentMessages = recentMessagesSnapshot.documents.map{
                    val message = it.toObject(FirebaseMessage::class.java)
                    if(message?.senderId == MainActivity.userId){
                        TextMessage.createForLocalUser(
                            message.text?:"",
                            System.currentTimeMillis()
                        )
                    } else {
                        TextMessage.createForRemoteUser(
                            message?.text?:"",
                            System.currentTimeMillis(),
                            message?.senderId?:"ABC"
                        )
                    }
                }
                val smartReplyGenerator = SmartReply.getClient()
                smartReplyGenerator.suggestReplies(recentMessages)
                    .addOnSuccessListener { result ->
                       if(result.status == SmartReplySuggestionResult.STATUS_SUCCESS){
                          viewModelScope.launch {
                              messageChannel.send(MessagingEvent.SmartReplySuccess(result.suggestions.map {
                                  it.text
                              }))
                          }
                       }
                    }
            }
        }
    }

    private val userId = mAuth.currentUser!!.uid
    val chatAdapter = ChatAdapter()

    fun onSendClick() {
        if (text?.isNotEmpty() == true) {
            viewModelScope.launch {
                val docRef = messagesRef.document()
                docRef.set(
                    FirebaseMessage(
                        senderId = userId,
                        text = text,
                        id = docRef.id,
                        username = MainActivity.userName
                    )
                ).addOnFailureListener {
                    viewModelScope.launch {
                        messageChannel.send(MessagingEvent.MessageError(it.localizedMessage!!))
                    }
                }
            }
        }
    }


    sealed class MessagingEvent {
        data class MessageError(val message: String) : MessagingEvent()
        data class SmartReplySuccess(val replies: List<String>): MessagingEvent()
    }
}