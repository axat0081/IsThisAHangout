package com.example.isthisahangout.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.repository.OpenAIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

const val OPEN_AI_CHAT_MESSAGE = "open_ai_chat_message"

@HiltViewModel
class OpenAIViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val openAIRepository: OpenAIRepository,
) : ViewModel() {
    val openAIChatMessage = savedStateHandle.getStateFlow(OPEN_AI_CHAT_MESSAGE, null)
    val chatMessages = openAIRepository.getOpenAIMessages(MainActivity.userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun setOnChatMessageChange(message: String) {
        savedStateHandle[OPEN_AI_CHAT_MESSAGE] = message
    }

}