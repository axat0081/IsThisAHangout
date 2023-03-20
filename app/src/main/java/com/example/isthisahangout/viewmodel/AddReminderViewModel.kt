package com.example.isthisahangout.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.reminders.Reminder
import com.example.isthisahangout.repository.UserRepository
import com.example.isthisahangout.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

const val REMINDER_NAME = "reminder_name"
const val REMINDER_DESC = "reminder_desc"
const val REMINDER_DAY = "reminder_day"
const val REMINDER_MONTH = "reminder_month"
const val REMINDER_YEAR = "reminder_year"
const val REMINDER_HOUR = "reminder_day"
const val REMINDER_MIN = "reminder_minute"

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val calendar = Calendar.getInstance()
    val reminderName = savedStateHandle.getStateFlow(REMINDER_NAME, "")
    val reminderDesc = savedStateHandle.getStateFlow(REMINDER_DESC, "")
    private val savedDay = savedStateHandle.getStateFlow(REMINDER_DAY, 0)
    private val savedMonth = savedStateHandle.getStateFlow(REMINDER_MONTH, 0)
    private val savedYear = savedStateHandle.getStateFlow(REMINDER_YEAR, 0)
    private val savedHour = savedStateHandle.getStateFlow(REMINDER_HOUR, 0)
    private val savedMinute = savedStateHandle.getStateFlow(REMINDER_MIN, 0)
    val reminderTime = combine(
        savedYear, savedMonth, savedDay, savedHour, savedMinute
    ) { year, month, day, hour, minute ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.timeInMillis
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)


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

    fun onDateSet(year: Int, month: Int, day: Int) {
        savedStateHandle[REMINDER_DAY] = day
        savedStateHandle[REMINDER_MONTH] = month
        savedStateHandle[REMINDER_YEAR] = year
    }

    fun onTimeSet(hour: Int, minute: Int) {
        savedStateHandle[REMINDER_HOUR] = hour
        savedStateHandle[REMINDER_MIN] = minute
    }

    fun onReminderAddClick() {
        if (reminderName.value.isBlank()) {
            viewModelScope.launch {
                addReminderEventChannel.send(AddReminderEvent.AddReminderFailure("Please add a reminder name"))
            }
        } else if (reminderDesc.value.isBlank()) {
            viewModelScope.launch {
                addReminderEventChannel.send(AddReminderEvent.AddReminderFailure("Please add a reminder description"))
            }
        } else if (reminderTime.value == 0L) {
            viewModelScope.launch {
                addReminderEventChannel.send(AddReminderEvent.AddReminderFailure("Please select a reminder time"))
            }
        } else {
            Log.e(
                "name",
                "${savedDay.value} ${savedMonth.value} ${savedYear.value} ${savedHour.value} ${savedMinute.value}"
            )
            userRepository.createReminder(
                MainActivity.userId, Reminder(
                    name = reminderName.value,
                    desc = reminderDesc.value,
                    time = reminderTime.value,
                    userId = MainActivity.userId
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
            }.launchIn(viewModelScope)
        }
    }

    sealed class AddReminderEvent {
        data class AddReminderSuccess(val message: String) : AddReminderEvent()
        data class AddReminderFailure(val message: String) : AddReminderEvent()
    }

}