package com.example.isthisahangout.viewmodel.detailScreen

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.cache.favourites.FavouritesDao
import com.example.isthisahangout.cache.posts.PostsDao
import com.example.isthisahangout.models.Comments
import com.example.isthisahangout.models.FirebasePost
import com.example.isthisahangout.models.LikedPostId
import com.example.isthisahangout.models.favourites.FavPost
import com.example.isthisahangout.repository.PostsRepository
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.utils.asFlow
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

const val POST = "post"

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val app: Application,
    private val favouritesDao: FavouritesDao,
    private val postsDao: PostsDao,
    private val savedStateHandle: SavedStateHandle,
    @Named("PostsRef") private val postsRef: CollectionReference,
    postsRepository: PostsRepository
) : ViewModel() {

    val post = savedStateHandle.get<FirebasePost>(POST) ?: FirebasePost(id = "123")
    var commentText = savedStateHandle.get<String>("comment_text")
        set(value) {
            field = value
            savedStateHandle["comment_text"] = commentText
        }

    var commentImage = savedStateHandle.get<Uri>("comment_image")
        set(value) {
            field = value
            savedStateHandle["comment_image"] = commentImage
        }

    val currentPost = postsRef.document(post.id ?: "ABC").asFlow().map {
        it.toObject(FirebasePost::class.java)
    }.stateIn(viewModelScope, SharingStarted.Lazily, FirebasePost())

    val comments = postsRepository.getPostComments(post.id)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val postsLikeBookMarkEventChannel = Channel<PostsLikeBookMarkEvent>()
    val postsLikeBookMarkEventFlow = postsLikeBookMarkEventChannel.receiveAsFlow()
    private val postCommentEventChannel = Channel<PostsCommentEvent>()
    val postsCommentEventFlow = postCommentEventChannel.receiveAsFlow()

    val isBookMarked = favouritesDao.getPosts("", MainActivity.userId).map { favPosts ->
        favPosts.any { it.id == post.id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val isLiked = postsDao.getLikesPostsId(MainActivity.userId).map { likedPosts ->
        likedPosts.any { it.postId == post.id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun onBookMarkClick() {
        if (isBookMarked.value) {
            viewModelScope.launch {
                post.id?.let {
                    favouritesDao.deletePost(
                        it,
                        MainActivity.userId
                    )
                    postsLikeBookMarkEventChannel.send(PostsLikeBookMarkEvent.RemovedFromBookMarks)
                }
            }
        } else {
            viewModelScope.launch {
                if (post.id != null && post.title != null) {
                    favouritesDao.insertPost(
                        FavPost(
                            id = post.id,
                            userId = MainActivity.userId,
                            title = post.title,
                            username = post.username,
                            pfp = post.pfp,
                            time = post.time,
                            text = post.text,
                            image = post.image
                        )
                    )
                    postsLikeBookMarkEventChannel.send(PostsLikeBookMarkEvent.AddedToBookMarks)
                }
            }
        }
    }

    fun onLikeClick() {
        if (post.id == null) return
        viewModelScope.launch {
            if (isLiked.value) {
                try {
                    postsRef.document(post.id).update("likes", FieldValue.increment(-1))
                        .await()
                    postsDao.deleteLikedPostId(userId = MainActivity.userId, id = post.id)
                } catch (exception: Exception) {
                    postsLikeBookMarkEventChannel.send(
                        PostsLikeBookMarkEvent
                            .PostsLikeError("Could not remove like: ${exception.localizedMessage}")
                    )
                }
            } else {
                try {
                    postsRef.document(post.id).update("likes", FieldValue.increment(1))
                        .await()
                    postsDao.insertLikedPostId(
                        LikedPostId(
                            postId = post.id,
                            userId = MainActivity.userId
                        )
                    )
                } catch (exception: Exception) {
                    postsLikeBookMarkEventChannel.send(
                        PostsLikeBookMarkEvent
                            .PostsLikeError("Could not like the post: ${exception.localizedMessage}")
                    )
                }
            }
        }
    }

    fun onCommentSendClick(post: FirebasePost) {
        if (commentText.isNullOrBlank()) {
            viewModelScope.launch {
                postCommentEventChannel.send(PostsCommentEvent.CommentSendFailure("Comment cannot be blank"))
            }
        } else {
            val comment = Comments(
                username = MainActivity.userName,
                text = commentText,
                pfp = MainActivity.userPfp,
                time = System.currentTimeMillis(),
                image = if (commentImage == null) null else commentImage.toString(),
                contentId = post.id
            )
            app.startService(
                Intent(app, FirebaseUploadService::class.java)
                    .putExtra(FirebaseUploadService.FIREBASE_COMMENT, comment)
                    .putExtra("path", "comment")
                    .setAction(FirebaseUploadService.ACTION_UPLOAD)
            )
            commentImage = null
        }
    }

    sealed class PostsLikeBookMarkEvent {
        object AddedToBookMarks : PostsLikeBookMarkEvent()
        object RemovedFromBookMarks : PostsLikeBookMarkEvent()
        data class PostsLikeError(val message: String) : PostsLikeBookMarkEvent()
    }

    sealed class PostsCommentEvent {
        data class CommentSentSuccess(val message: String) : PostsCommentEvent()
        data class CommentSendFailure(val message: String) : PostsCommentEvent()
    }
}