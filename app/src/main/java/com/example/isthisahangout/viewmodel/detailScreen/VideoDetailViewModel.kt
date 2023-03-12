package com.example.isthisahangout.viewmodel.detailScreen

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.cache.favourites.FavouritesDao
import com.example.isthisahangout.cache.videos.VideosDao
import com.example.isthisahangout.models.Comments
import com.example.isthisahangout.models.FirebaseVideo
import com.example.isthisahangout.models.LikedVideoId
import com.example.isthisahangout.models.favourites.FavVideo
import com.example.isthisahangout.repository.CommentsRepository
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.utils.asFlow
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

const val VIDEO = "video"

@HiltViewModel
class VideoDetailViewModel @Inject constructor(
    private val app: Application,
    private val favouritesDao: FavouritesDao,
    private val savedStateHandle: SavedStateHandle,
    private val videosDao: VideosDao,
    @Named("VideoRef")
    private val videosRef: CollectionReference,
    commentsRepository: CommentsRepository,
    @Named("VideoPlayer") val player: Player,
) : ViewModel() {
    val video = savedStateHandle.get<FirebaseVideo>(VIDEO)!!


    init {
        player.prepare()
        player.addMediaItem(MediaItem.fromUri(video.url!!.toUri()))
    }

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
    val commentImage = savedStateHandle.getStateFlow<String?>("comment_image", null)


    val comments = commentsRepository.getVideosComments(video.id)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val currentVideo = videosRef.document(video.id ?: "ABC").asFlow().map {
        it.toObject(FirebaseVideo::class.java)
    }.stateIn(viewModelScope, SharingStarted.Lazily, FirebaseVideo())

    val isBookMarked = favouritesDao.getVideos("", MainActivity.userId).map { favVideos ->
        favVideos.any { it.id == video.id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val isLiked = videosDao.getLikedVideos(MainActivity.userId).map { likedVideos ->
        likedVideos.any { it.videoId == video.id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _showDetails = MutableStateFlow(false)
    val showDetails = _showDetails.asStateFlow()

    private val videosLikeBookMarkEventChannel = Channel<VideoLikeBookMarkEvent>()
    val videosLikeBookMarkEventFlow = videosLikeBookMarkEventChannel.receiveAsFlow()
    private val videosCommentEventChannel = Channel<VideosCommentEvent>()
    val videosCommentEventFlow = videosCommentEventChannel.receiveAsFlow()

    fun onShowDetailsClick() {
        _showDetails.value = !_showDetails.value
    }

    fun setCommentImage(uri: Uri?) {
        savedStateHandle["comment_image"] = uri?.toString()
    }

    fun onLikeClick() {
        if (video.id == null) return
        if (isLiked.value) {
            viewModelScope.launch {
                try {
                    videosRef.document(video.id).update("likes", FieldValue.increment(-1))
                        .await()
                    videosDao.deleteLikedVideo(video.id, MainActivity.userId)
                } catch (exception: Exception) {
                    videosLikeBookMarkEventChannel.send(VideoLikeBookMarkEvent.VideosLikeError("Could not remove like: ${exception.localizedMessage}"))
                }
            }
        } else {
            viewModelScope.launch {
                try {
                    videosRef.document(video.id).update("likes", FieldValue.increment(+1))
                        .await()
                    videosDao.insert(LikedVideoId(videoId = video.id, userId = MainActivity.userId))
                } catch (exception: Exception) {
                    videosLikeBookMarkEventChannel.send(VideoLikeBookMarkEvent.VideosLikeError("Could not like the video: ${exception.localizedMessage}"))
                }
            }
        }
    }

    fun onBookMarkClick() {
        if (video.id == null) return
        if (isBookMarked.value) {
            viewModelScope.launch {
                favouritesDao.deleteVideo(
                    id = video.id,
                    userId = MainActivity.userId
                )
            }
        } else {
            viewModelScope.launch {
                favouritesDao.insertVideo(
                    FavVideo(
                        id = video.id,
                        userId = MainActivity.userId,
                        title = video.title,
                        time = video.time,
                        text = video.text,
                        pfp = video.pfp,
                        thumbnail = video.thumbnail,
                        url = video.url,
                        username = video.username,
                    )
                )
            }
        }
    }


    fun onCommentSendClick(video: FirebaseVideo) {
        if (commentText.isNullOrBlank() && commentImage.value == null) {
            viewModelScope.launch {
                videosCommentEventChannel.send(VideosCommentEvent.CommentSendFailure("Comment cannot be blank"))
            }
        } else {
            val comment = Comments(
                username = MainActivity.userName,
                text = commentText,
                pfp = MainActivity.userPfp,
                time = System.currentTimeMillis(),
                image = if (commentImage.value == null) null else commentImage.toString(),
                contentId = video.id,
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
            setCommentImage(null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }

    sealed class VideoLikeBookMarkEvent {
        object AddedToBookMarks : VideoLikeBookMarkEvent()
        object RemovedFromBookMarks : VideoLikeBookMarkEvent()
        data class VideosLikeError(val message: String) : VideoLikeBookMarkEvent()
    }

    sealed class VideosCommentEvent {
        data class CommentSentSuccess(val message: String) : VideosCommentEvent()
        data class CommentSendFailure(val message: String) : VideosCommentEvent()
    }
}