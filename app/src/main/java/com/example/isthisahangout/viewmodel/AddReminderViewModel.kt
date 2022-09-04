package com.example.isthisahangout.viewmodel

import Reminder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.repository.UserRepository
import com.example.isthisahangout.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val REMINDER_NAME = "reminder_name"
const val REMINDER_DESC = "reminder_desc"
const val REMINDER_TIME = "reminder_time"

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val reminderName = savedStateHandle.getStateFlow(REMINDER_NAME, "")
    val reminderDesc = savedStateHandle.getStateFlow(REMINDER_DESC, "")
    val reminderTime = savedStateHandle.getStateFlow(REMINDER_TIME, 0L)
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val addReminderEventChannel = Channel<AddReminderEvent>()
    val addReminderEventFlow = addReminderEventChannel.receiveAsFlow()

    fun setReminderName(name: String) {
        savedStateHandle[REMINDER_NAME] = name
    }

    fun setReminderDesc(desc: String) {
        savedStateHandle[REMINDER_DESC] = desc
    }

    fun setReminderTime(time: Long) {
        savedStateHandle[REMINDER_TIME] = time
    }

    fun onReminderAddClick() {
        viewModelScope.launch {
            if (reminderName.value.isBlank()) {
                addReminderEventChannel.send(AddReminderEvent.AddReminderFailure("Please add a reminder name"))
            } else if (reminderDesc.value.isBlank()) {
                addReminderEventChannel.send(AddReminderEvent.AddReminderFailure("Please add a reminder description"))
            } else if (reminderTime.value == 0L) {
                addReminderEventChannel.send(AddReminderEvent.AddReminderFailure("Please select a reminder time"))
            } else {
                userRepository.createReminder(
                    MainActivity.userId, Reminder(
                        name = reminderName.value,
                        desc = reminderDesc.value,
                        time = reminderTime.value
                    )
                ).onEach { result ->
                    _isLoading.value = result is Resource.Loading
                    if (result is Resource.Error) {
                        addReminderEventChannel.send(
                            AddReminderEvent.AddReminderFailure(
                                result.error?.localizedMessage ?: "Aw snap an error occurred"
                            )
                        )
                    } else if (result is Resource.Success) {
                        addReminderEventChannel.send(AddReminderEvent.AddReminderSuccess("Reminder added "))
                    }
                }
            }
        }
    }

    sealed class AddReminderEvent {
        data class AddReminderSuccess(val message: String) : AddReminderEvent()
        data class AddReminderFailure(val message: String) : AddReminderEvent()
    }

}