package com.example.isthisahangout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.reminders.Reminder
import com.example.isthisahangout.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val remindersUpdateIsDoneEventChannel = Channel<RemindersUpdateIsDoneEvent>()
    val reminderUpdateIsDoneEventFlow = remindersUpdateIsDoneEventChannel.receiveAsFlow()

    val reminders = userRepository.getReminders(MainActivity.userId)
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )

    fun onCheckBoxClick(reminder: Reminder, isChecked: Boolean) {
        if(reminder.id == null) {
            return
        }
        viewModelScope.launch {
            try {
                userRepository.updateReminder(
                    userId = MainActivity.userId,
                    reminderId = reminder.id,
                    isChecked = isChecked
                )
            } catch (exception: Exception) {
                remindersUpdateIsDoneEventChannel.send(
                    RemindersUpdateIsDoneEvent.ReminderUpdateIsDoneFailure(
                        message = exception.localizedMessage ?: "An unknown error occurred"
                    )
                )
            }
        }
    }

    sealed class RemindersUpdateIsDoneEvent {
        data class ReminderUpdateIsDoneFailure(val message: String) : RemindersUpdateIsDoneEvent()
    }
}