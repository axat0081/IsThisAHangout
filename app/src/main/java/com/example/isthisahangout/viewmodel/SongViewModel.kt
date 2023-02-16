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
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.Song
import com.example.isthisahangout.models.SongDto
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.utils.asResourceFlow
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SongViewModel @Inject constructor(
    private val app: Application,
    private val state: SavedStateHandle,
    @Named("SongRef")
    private val musicCollectionRef: CollectionReference
) : AndroidViewModel(app) {

    private val songChannel = Channel<SongEvent>()
    val songEventFlow = songChannel.receiveAsFlow()

    var songTitle = state.get<String>("song_title") ?: ""
        set(value) {
            field = value
            state["song_title"] = songTitle
        }

    var songText = state.get<String>("song_title") ?: ""
        set(value) {
            field = value
            state["song_text"] = songText
        }

    var songUrl = state.get<Uri>("song_url")
        set(value) {
            field = value
            state["song_url"] = songUrl
        }

    var songThumbnail = state.get<Uri>("song_thumbnail")
        set(value) {
            field = value
            state["song_thumbnail"] = songThumbnail
        }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            songUploadResult(intent)
        }
    }

    val songs = musicCollectionRef.asResourceFlow {
        it.toObjects(SongDto::class.java)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onUploadClick() {
        if (songTitle.isBlank()) {
            viewModelScope.launch {
                songChannel.send(SongEvent.UploadSongError("Please add a title"))
            }
        } else if (songUrl == null) {
            viewModelScope.launch {
                songChannel.send(SongEvent.UploadSongError("Please select a song"))
            }
        } else if (songThumbnail == null) {
            viewModelScope.launch {
                songChannel.send(SongEvent.UploadSongError("Please select a thumbnail"))
            }
        } else {
            Log.e("Music", "Song upload")
            val song = Song(
                id = "NA",
                time = System.currentTimeMillis(),
                title = songTitle,
                text = songText,
                pfp = MainActivity.userPfp,
                username = MainActivity.userName,
                thumbnail = songThumbnail.toString(),
                url = songUrl.toString()
            )
            app.startService(
                Intent(app, FirebaseUploadService::class.java)
                    .putExtra(FirebaseUploadService.FIREBASE_SONG, song)
                    .putExtra("path", "song")
                    .setAction(FirebaseUploadService.ACTION_UPLOAD)
            )
        }
    }

    private fun songUploadResult(intent: Intent) {
        viewModelScope.launch {
            songUrl = intent.getParcelableExtra(FirebaseUploadService.EXTRA_DOWNLOAD_URL)
            if (songUrl == null) {
                songChannel.send(SongEvent.UploadSongError("Song could not be uploaded"))
            } else {
                songChannel.send(SongEvent.UploadSongSuccess("Song Uploaded"))
            }
        }
    }



    sealed class SongEvent {
        data class UploadSongSuccess(val message: String) : SongEvent()
        data class UploadSongError(val message: String) : SongEvent()
    }
}