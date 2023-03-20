package com.example.isthisahangout.viewmodel.detailScreen

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.Comments
import com.example.isthisahangout.models.Song
import com.example.isthisahangout.repository.CommentsRepository
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Long.max
import java.lang.Long.min
import javax.inject.Inject
import javax.inject.Named

const val SONG = "song"

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val app: Application,
    private val savedStateHandle: SavedStateHandle,
    commentsRepository: CommentsRepository,
    @Named("SongPlayer")
    private val songPlayer: Player,
) : AndroidViewModel(app) {
    val song = savedStateHandle.get<Song>(SONG)!!
    val songDuration = savedStateHandle.getStateFlow("song_duration", 0L)

    init {
        songPlayer.prepare()
        songPlayer.addMediaItem(MediaItem.fromUri(song.url.toUri()))
        songPlayer.playWhenReady = true
        songPlayer.play()
        songPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY) {
                    savedStateHandle["song_duration"] = songPlayer.duration
                }
            }
        })
    }

    private val songPosition = flow {
        delay(1000)
        emit(songPlayer.currentPosition)
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    val seekbarPosition = combine(songPosition, songDuration) { (position, duration)->
        Pair(position, duration)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

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

    fun seekTo(position: Long) {
        songPlayer.seekTo(position)
    }

    fun play() = songPlayer.play()

    fun pause() = songPlayer.pause()

    fun isPlaying() = songPlayer.isPlaying

    fun rewind() {
        val position = max(0, songPlayer.currentPosition - 10 * 10000)
        songPlayer.seekTo(position)
    }

    fun forward() {
        val position = min(songDuration.value, songPlayer.currentPosition + 10 * 1000)
        songPlayer.seekTo(position)
    }

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

    override fun onCleared() {
        super.onCleared()
        songPlayer.release()
    }

    sealed class SongEvent {
        data class SongError(val message: String) : SongEvent()
    }
}