package com.example.isthisahangout.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.Comments
import com.example.isthisahangout.models.FirebasePost
import com.example.isthisahangout.models.LikedPostId
import com.example.isthisahangout.models.favourites.FavPost
import com.example.isthisahangout.pagingsource.PostsPagingSource
import com.example.isthisahangout.cache.favourites.FavouritesDao
import com.example.isthisahangout.cache.posts.PostsDao
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TITLE_EMPTY = "Please give a post title"

@HiltViewModel
class PostViewModel @Inject constructor(
    private val app: Application,
    private val state: SavedStateHandle,
    private val favouritesDao: FavouritesDao,
    private val postsDao: PostsDao,
    private val mAuth: FirebaseAuth
) : AndroidViewModel(app) {

    private val postChannel = Channel<PostsEvent>()
    val postsEventFlow = postChannel.receiveAsFlow()
    val isBookMarked = MutableLiveData(false)

    var postTitle = state.get<String>("post_title") ?: ""
        set(value) {
            field = value
            state.set("post_title", postTitle)
        }

    var postText = state.get<String>("post_text") ?: ""
        set(value) {
            field = value
            state.set("post_text", postText)
        }

    var postImage: Uri? = state.get<Uri>("postImage")
        set(value) {
            field = value
            state.set("postImage", postImage)
        }

    var commentText = state.get<String>("comment_text")
        set(value) {
            field = value
            state.set("comment_text", commentText)
        }

    var commentImage = state.get<Uri>("comment_image")
        set(value) {
            field = value
            state.set("comment_image", commentImage)
        }


    val currentPostId = MutableStateFlow("abc")

    val isLiked = MutableStateFlow(false)

    val likedPost = currentPostId.flatMapLatest { postId ->
        postsDao.getLikesPostsId(mAuth.uid!!, postId)
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.e("FirebaseAuthViewModel", "onReceive:$intent")
            postImageUploadResult(intent)
        }
    }

    val postsFlow = Pager(PagingConfig(20)) {
        PostsPagingSource()
    }.flow.cachedIn(viewModelScope)


    fun onBookMarkClick(post: FirebasePost) {
        isBookMarked.value = !isBookMarked.value!!
        if (isBookMarked.value!!) {
            viewModelScope.launch {
                favouritesDao.insertPost(
                    FavPost(
                        id = post.id!!,
                        text = post.text,
                        image = post.image,
                        username = post.username,
                        pfp = post.username,
                        time = post.time,
                        userId = MainActivity.userId,
                        title = post.title!!
                    )
                )
            }
        } else {
            viewModelScope.launch {
                favouritesDao.deletePost(
                    id = post.id!!,
                    userId = MainActivity.userId
                )
            }
        }
    }

    fun onLikeClick(post: FirebasePost) {
        viewModelScope.launch {
            if (isLiked.value) {
                postsDao.deleteLikedPostId(mAuth.uid!!, post.id!!)
            } else {
                postsDao.insertLikedPostId(
                    LikedPostId(
                        postId = post.id!!,
                        userId = mAuth.uid!!
                    )
                )
            }
            isLiked.value = !isLiked.value
        }
    }

    fun onCommentSendClick(post: FirebasePost) {
        if (commentText.isNullOrBlank()) {
            viewModelScope.launch {
                postChannel.send(PostsEvent.CreatePostError("Comment cannot be blank"))
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

    fun onCreatePostClick() {
        if (postTitle.isBlank()) {
            viewModelScope.launch {
                postChannel.send(PostsEvent.CreatePostError(TITLE_EMPTY))
            }
            return
        }
        val post =
            FirebasePost(
                title = postTitle,
                text = postText,
                image = postImage.toString(),
                likes = 0,
                time = System.currentTimeMillis(),
                pfp = MainActivity.userPfp,
                username = MainActivity.userName
            )
        app.startService(
            Intent(app, FirebaseUploadService::class.java)
                .putExtra(FirebaseUploadService.FIREBASE_POST, post)
                .putExtra("path", "postImage")
                .setAction(FirebaseUploadService.ACTION_UPLOAD).apply {
                    Log.e("FirebaseAuthViewModel", this.toString())
                }
        )
    }

    private fun postImageUploadResult(intent: Intent) {
        viewModelScope.launch {
            postImage = intent.getParcelableExtra(FirebaseUploadService.EXTRA_DOWNLOAD_URL)
            if (postImage == null) {
                postChannel.send(PostsEvent.CreatePostError("Aw snap, an error occurred"))
            } else {
                viewModelScope.launch {
                    postChannel.send(
                        PostsEvent.CreatePostSuccess(
                            "Posted Successfully"
                        )
                    )
                }
            }
        }
    }

    sealed class PostsEvent {
        data class CreatePostSuccess(val message: String) : PostsEvent()
        data class CreatePostError(val message: String) : PostsEvent()
    }
}