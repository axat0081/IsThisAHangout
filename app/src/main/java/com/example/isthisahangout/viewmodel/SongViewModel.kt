package com.example.isthisahangout.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.Song
import com.example.isthisahangout.models.SongDto
import com.example.isthisahangout.pagingsource.SongPagingSource
import com.example.isthisahangout.service.music.MusicServiceConnection
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.utils.Constants.MEDIA_ROOT_ID
import com.example.isthisahangout.utils.MusicResource
import com.example.isthisahangout.utils.isPlayEnabled
import com.example.isthisahangout.utils.isPlaying
import com.example.isthisahangout.utils.isPrepared
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val app: Application,
    private val state: SavedStateHandle,
    private val musicServiceConnection: MusicServiceConnection
) : AndroidViewModel(app) {

    private val songChannel = Channel<SongEvent>()
    val songEventFlow = songChannel.receiveAsFlow()

    var songTitle = state.get<String>("song_title") ?: ""
        set(value) {
            field = value
            state.set("song_title", songTitle)
        }

    var songText = state.get<String>("song_title") ?: ""
        set(value) {
            field = value
            state.set("song_text", songText)
        }

    var songUrl = state.get<Uri>("song_url")
        set(value) {
            field = value
            state.set("song_url", songUrl)
        }

    var songThumbnail = state.get<Uri>("song_thumbnail")
        set(value) {
            field = value
            state.set("song_thumbnail", songThumbnail)
        }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.e("FirebaseAuthViewModel", "onReceive:$intent")
            songUploadResult(intent)
        }
    }

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val playBackState = musicServiceConnection.playbackState
    val currentPlayingSong = musicServiceConnection.currentPlayingSong

    private val _mediaItems =
        MutableStateFlow<MusicResource<List<SongDto>>>(MusicResource.loading(null))
    val mediaItems = _mediaItems.asStateFlow()

    init {
        musicServiceConnection.subscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val songDtoList = children.map { mediaItem ->
                        SongDto(
                            mediaId = mediaItem.mediaId!!,
                            title = mediaItem.description.title.toString(),
                            subtitle = mediaItem.description.subtitle.toString(),
                            songUrl = mediaItem.description.mediaUri.toString(),
                            imageUrl = mediaItem.description.iconUri.toString()
                        )
                    }
                    _mediaItems.value = MusicResource.success(songDtoList)
                }
            })
    }

    fun skipToNext() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPrevious() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(position: Long) {
        musicServiceConnection.transportControls.seekTo(position)
    }

    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false) {
        val isPrepared = playBackState.value?.isPrepared ?: false
        if (isPrepared &&
            mediaItem.id == currentPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)
        ) {
            playBackState.value?.let { playBackState ->
                when {
                    playBackState.isPlaying ->
                        if (toggle) musicServiceConnection.transportControls.pause()
                    playBackState.isPlayEnabled ->
                        musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.id, null)
        }
    }

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

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {

            })
    }

    sealed class SongEvent {
        data class UploadSongSuccess(val message: String) : SongEvent()
        data class UploadSongError(val message: String) : SongEvent()
    }
}