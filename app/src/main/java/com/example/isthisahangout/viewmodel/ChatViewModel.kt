package com.example.isthisahangout.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.adapter.chat.ChatAdapter
import com.example.isthisahangout.models.FirebaseMessage
import com.example.isthisahangout.pagingsource.MessagesPagingSource
import com.example.isthisahangout.pagingsource.PostsPagingSource
import com.example.isthisahangout.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
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
    mAuth: FirebaseAuth,
    @Named("MessagesRef") private val messagesRef: CollectionReference,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val messageChannel = Channel<MessagingEvent>()
    val messageEventFlow = messageChannel.receiveAsFlow()
    var text = state.get<String>("message_text")
        set(value) {
            field = value
            state["message_text"] = text
        }

    init {
        loadMoreMessages()
//        viewModelScope.launch {
//            messagesQuery.asFlow().collectLatest { recentMessagesSnapshot ->
//                val recentMessages = recentMessagesSnapshot.documents.map {
//                    val message = it.toObject(FirebaseMessage::class.java)
//                    if (message?.senderId == MainActivity.userId) {
//                        TextMessage.createForLocalUser(
//                            message.text ?: "",
//                            System.currentTimeMillis()
//                        )
//                    } else {
//                        TextMessage.createForRemoteUser(
//                            message?.text ?: "",
//                            System.currentTimeMillis(),
//                            message?.senderId ?: "ABC"
//                        )
//                    }
//                }
//                Log.e("newMessages", recentMessages.toString())
//                val smartReplyGenerator = SmartReply.getClient()
//                smartReplyGenerator.suggestReplies(recentMessages)
//                    .addOnSuccessListener { result ->
//                        if (result.status == SmartReplySuggestionResult.STATUS_SUCCESS) {
//                            viewModelScope.launch {
//                                messageChannel.send(MessagingEvent.SmartReplySuccess(result.suggestions.map {
//                                    it.text
//                                }))
//                            }
//                        }
//                    }
//            }
//        }
    }

    val messagesPaged = Pager(PagingConfig(10)) {
        MessagesPagingSource()
    }.flow.cachedIn(viewModelScope)
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
                        messageChannel.send(
                            MessagingEvent.MessageError(
                                it.localizedMessage ?: "Network error, check internet connection"
                            )
                        )
                    }
                }
            }
        }
    }

    private var lastLoadedMessage: FirebaseMessage? = null
    private val _oldChatMessages = MutableStateFlow(emptyList<FirebaseMessage>())
    val oldChatMessages = _oldChatMessages.asStateFlow()
    val newMessages =
        chatRepository.loadNewMessages().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _messagesLoadingState = MutableStateFlow(MessagesLoadState.LOADING_INITIAL)
    val messagesLoadingState = _messagesLoadingState.asStateFlow()

    var isEndOfChatPagination = false

    private var _loadingMoreError = MutableStateFlow<String?>(null)
    val loadingMoreError = _loadingMoreError.asStateFlow()
    private var _loadingInitialError = MutableStateFlow<String?>(null)
    val loadingInitialError = _loadingInitialError.asStateFlow()


    fun loadMoreMessages() {
        viewModelScope.launch {
            try {
                _messagesLoadingState.value =
                    if (lastLoadedMessage == null) MessagesLoadState.LOADING_INITIAL else MessagesLoadState.LOADING_MORE
                var messages = chatRepository.loadOldMessages(lastLoadedMessage)
                messages = messages.reversed()
                _messagesLoadingState.value = MessagesLoadState.NOT_LOADING
                if (messages.isEmpty()) {
                    isEndOfChatPagination = true
                }
                lastLoadedMessage = messages.lastOrNull()
                _oldChatMessages.value += messages
            } catch (exception: Exception) {
                _messagesLoadingState.value = MessagesLoadState.NOT_LOADING
                if (lastLoadedMessage == null) {
                    _loadingInitialError.value = exception.localizedMessage
                } else {
                    _loadingMoreError.value = exception.localizedMessage
                }
            }
        }
    }

    enum class MessagesLoadState {
        LOADING_INITIAL, LOADING_MORE, NOT_LOADING
    }

    sealed class MessagingEvent {
        data class MessageError(val message: String) : MessagingEvent()
    }
}