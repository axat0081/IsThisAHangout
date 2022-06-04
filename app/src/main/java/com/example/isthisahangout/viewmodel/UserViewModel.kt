package com.example.isthisahangout.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.models.ComfortCharacter
import com.example.isthisahangout.repository.UserRepository
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class UserViewModel @Inject constructor(
    private val app: Application,
    private val state: SavedStateHandle,
    private val userRepository: UserRepository
) : AndroidViewModel(app) {

    var comfortCharacterName = state.get<String>("comfort_character_name")
        set(value) {
            field = value
            state.set("comfort_character_name", comfortCharacterName)
        }

    var comfortCharacterPic: Uri? = state.get<Uri>("comfort_character_pic")
        set(value) {
            field = value
            state.set("comfort_character_pic", comfortCharacterPic)
        }

    var comfortCharacterFrom = state.get<String>("comfort_character_from")
        set(value) {
            field = value
            state.set("comfort_character_from", comfortCharacterFrom)
        }

    var comfortCharacterDesc = state.get<String>("comfort_character_desc")
        set(value) {
            field = value
            state.set("comfort_character_desc", comfortCharacterDesc)
        }

    var comfortCharacterPrioirty = state.get<Int>("comfort_character_priority") ?: 0
        set(value) {
            field = value
            state.set("comfort_character_priority", comfortCharacterPrioirty)
        }
    val userId = MutableStateFlow("");
    private val databaseEventChannel = Channel<DatabaseEvent>()
    val databaseEventFlow = databaseEventChannel.receiveAsFlow()
    val comfortCharacters = userId.flatMapLatest { userid ->
        if (userid.isNotEmpty()) userRepository.getComfortCharacters(userid) else emptyFlow()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.e("FirebaseAuthViewModel", "onReceive:$intent")
            addComfortCharacterResult(intent)
        }
    }

    fun onAddComfortCharacterClick() {
        if (comfortCharacterName.isNullOrBlank()) {
            viewModelScope.launch {
                databaseEventChannel.send(DatabaseEvent.DatabaseFailure("Comfort Character name cannot be blank"))
            }
        } else if (comfortCharacterDesc.isNullOrBlank()) {
            viewModelScope.launch {
                databaseEventChannel.send(DatabaseEvent.DatabaseFailure("Comfort Character description cannot be empty"))
            }
        } else if (comfortCharacterPic == null) {
            viewModelScope.launch {
                databaseEventChannel.send(DatabaseEvent.DatabaseFailure("Please select an image of your comfort character"))
            }
        } else if (comfortCharacterFrom.isNullOrBlank()) {
            viewModelScope.launch {
                databaseEventChannel.send(DatabaseEvent.DatabaseFailure("Please enter where your comfort character is from"))
            }
        } else {
            val character = ComfortCharacter(
                name = comfortCharacterName!!,
                desc = comfortCharacterDesc!!,
                from = comfortCharacterFrom!!,
                image = comfortCharacterPic.toString(),
                priority = comfortCharacterPrioirty
            )
            viewModelScope.launch {
                app.startService(
                    Intent(app, FirebaseUploadService::class.java)
                        .putExtra(FirebaseUploadService.FIREBASE_COMFORT_CHARACTER, character)
                        .putExtra("path", "comfortCharacter")
                        .setAction(FirebaseUploadService.ACTION_UPLOAD).apply {
                        }
                )
            }
        }
    }

    fun addComfortCharacterResult(intent: Intent) {
        comfortCharacterPic = intent.getParcelableExtra(FirebaseUploadService.EXTRA_DOWNLOAD_URL)
        viewModelScope.launch {
            if (comfortCharacterPic == null) {
                databaseEventChannel.send(DatabaseEvent.DatabaseFailure("Comfort character Could not be uploaded. Please try again later"))
            } else {
                databaseEventChannel.send(DatabaseEvent.DatabaseSuccess("Your comfort character has been uploaded"))
            }
        }
    }

    sealed class DatabaseEvent {
        data class DatabaseSuccess(val msg: String) : DatabaseEvent()
        data class DatabaseFailure(val msg: String) : DatabaseEvent()
    }
}