package com.example.isthisahangout.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.repository.OpenAIRepository
import com.example.isthisahangout.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

const val OPEN_AI_CHAT_MESSAGE = "open_ai_chat_message"

@HiltViewModel
class OpenAIViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val openAIRepository: OpenAIRepository,
) : ViewModel() {
    val openAIChatMessage = savedStateHandle.getStateFlow(OPEN_AI_CHAT_MESSAGE, "")
    private val openAIChatEvenChannel = Channel<OpeAIChatEvent>()
    val openAIChannelFlow = openAIChatEvenChannel.receiveAsFlow()

    val chatMessages = openAIRepository.getOpenAIMessages(MainActivity.userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun setOnChatMessageChange(message: String) {
        savedStateHandle[OPEN_AI_CHAT_MESSAGE] = message
    }

    fun onSendClick() {
        val message = openAIChatMessage.value
        if (message.isBlank()) {
            viewModelScope.launch {
                openAIChatEvenChannel.send(OpeAIChatEvent.MessageSendFailure("Prompt cannot be empty"))
            }
        } else {
            viewModelScope.launch {
                val result = openAIRepository.getResponse(userMessage = message, aiUserName = "")
                if(result is Resource.Error) {
                    openAIChatEvenChannel.send(OpeAIChatEvent.MessageSendFailure(result.error?.localizedMessage?:"Aw snap an error occurred"))
                }
            }
        }
    }

    sealed class OpeAIChatEvent {
        data class MessageSendFailure(val message: String) : OpeAIChatEvent()
    }
}