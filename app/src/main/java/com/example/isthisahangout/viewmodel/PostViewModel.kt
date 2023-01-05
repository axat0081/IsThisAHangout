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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.FirebasePost
import com.example.isthisahangout.pagingsource.PostsPagingSource
import com.example.isthisahangout.repository.CommentsRepository
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TITLE_EMPTY = "Please give a post title"

@HiltViewModel
class PostViewModel @Inject constructor(
    private val app: Application,
    private val state: SavedStateHandle,
    postsRepository: CommentsRepository
) : AndroidViewModel(app) {

    private val postChannel = Channel<PostsEvent>()
    val postsEventFlow = postChannel.receiveAsFlow()

    var postTitle = state.get<String>("post_title") ?: ""
        set(value) {
            field = value
            state["post_title"] = postTitle
        }

    var postText = state.get<String>("post_text") ?: ""
        set(value) {
            field = value
            state["post_text"] = postText
        }

    var postImage: Uri? = state.get<Uri>("postImage")
        set(value) {
            field = value
            state["postImage"] = postImage
        }


    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            postImageUploadResult(intent)
        }
    }

    val postsFlow = Pager(PagingConfig(20)) {
        PostsPagingSource()
    }.flow.cachedIn(viewModelScope)

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