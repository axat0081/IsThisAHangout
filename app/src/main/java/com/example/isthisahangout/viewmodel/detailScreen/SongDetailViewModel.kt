package com.example.isthisahangout.viewmodel.detailScreen

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.Comments
import com.example.isthisahangout.models.Song
import com.example.isthisahangout.repository.CommentsRepository
import com.example.isthisahangout.service.music.MusicServiceConnection
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.utils.convertToPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SONG = "song"

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val app: Application,
    private val savedStateHandle: SavedStateHandle,
    commentsRepository: CommentsRepository,
    private val musicServiceConnection: MusicServiceConnection,
) : AndroidViewModel(app) {
    val song = savedStateHandle.get<Song>(SONG)!!
    val musicState = musicServiceConnection.musicState
    val currentPosition = musicServiceConnection.currentPosition
        .stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    init {
        musicServiceConnection.playSong(song)
    }

    fun skipPrevious() = musicServiceConnection.skipPrevious()
    fun play() = musicServiceConnection.play()
    fun pause() = musicServiceConnection.pause()
    fun skipNext() = musicServiceConnection.skipNext()
    fun skipTo(position: Float) =
        musicServiceConnection.skipTo(convertToPosition(position, musicState.value.duration))

    private val songEventChannel = Channel<SongEvent>()
    val songEventFlow = songEventChannel.receiveAsFlow()

    private val _showDetails = MutableStateFlow(false)
    val showDetails = _showDetails.asStateFlow()

    var replyingToCommentId = savedStateHandle.get<String>("replying_to_comment_id")
        set(value) {
            field = value
            savedStateHandle["replying_to_comment_id"] = replyingToCommentId
        }

    var replyingToUserId = savedStateHandle.get<String>("replying_to_user_id")
        set(value) {
            field = value
            savedStateHandle["replying_to_user_id"] = replyingToUserId
        }

    var replyingToUserName = savedStateHandle.get<String>("replying_to_user_name")
        set(value) {
            field = value
            savedStateHandle["replying_to_user_name"] = replyingToUserName
        }

    var replyingToPfp = savedStateHandle.get<String>("replying_to_user_pfp")
        set(value) {
            field = value
            savedStateHandle["replying_to_user_pfp"] = replyingToPfp
        }

    var replyingToText = savedStateHandle.get<String>("replying_to_user_text")
        set(value) {
            field = value
            savedStateHandle["replying_to_user_text"] = replyingToText
        }

    var commentText = savedStateHandle.get<String>("comment_text")
        set(value) {
            field = value
            savedStateHandle["comment_text"] = commentText
        }
    var commentImage = savedStateHandle.getStateFlow<String?>("comment_image", null)

    val comments = commentsRepository.getSongComments(song.id)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun onShowDetailsClick() {
        _showDetails.value = !_showDetails.value
    }

    fun setCommentImageUri(uri: Uri?) {
        if (uri == null) {
            savedStateHandle["comment_image"] = null
        }
        savedStateHandle["comment_image"] = uri.toString()
    }

    fun onCommentSendClick(song: Song) {
        if (commentText.isNullOrBlank() && commentImage.value == null) {
            viewModelScope.launch {
                songEventChannel.send(SongEvent.SongError("Comment cannot be blank"))
            }
        } else {
            val comment = Comments(
                username = MainActivity.userName,
                text = commentText,
                pfp = MainActivity.userPfp,
                time = System.currentTimeMillis(),
                image = if (commentImage.value == null) null else commentImage.toString(),
                contentId = song.id,
                replyingToCommentId = replyingToCommentId,
                replyingToUserId = replyingToUserId,
                replyingToUserName = replyingToUserName,
                replyingToText = replyingToText,
                replyingToPfp = replyingToPfp
            )
            app.startService(
                Intent(app, FirebaseUploadService::class.java)
                    .putExtra(FirebaseUploadService.FIREBASE_COMMENT, comment)
                    .putExtra("path", "comment")
                    .setAction(FirebaseUploadService.ACTION_UPLOAD)
            )
            setCommentImageUri(null)
        }
    }

    sealed class SongEvent {
        data class SongError(val message: String) : SongEvent()
    }
}