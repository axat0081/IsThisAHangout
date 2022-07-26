package com.example.isthisahangout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val reminders = userRepository.getReminders(MainActivity.userId)
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )

}