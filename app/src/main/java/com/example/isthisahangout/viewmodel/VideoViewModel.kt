package com.example.isthisahangout.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.FirebaseVideo
import com.example.isthisahangout.pagingsource.VideosPagingSource
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val app: Application,
    private val state: SavedStateHandle,
) : AndroidViewModel(app) {

    private val videoEventChannel = Channel<VideoEvent>()
    val videoEventFlow = videoEventChannel.receiveAsFlow()

    val showDetails = MutableLiveData(false)

    var videoTitle = state.get<String>("video_title") ?: ""
        set(value) {
            field = value
            state.set("video_title", videoTitle)
        }
    var videoText = state.get<String>("video_text") ?: ""
        set(value) {
            field = value
            state.set("video_text", videoText)
        }

    var videoUrl = state.get<Uri>("video_url")
        set(value) {
            field = value
            state.set("video_url", videoUrl)
        }

    var videoThumbnail = state.get<Uri>("video_thumbnail")
        set(value) {
            field = value
            state.set("video_thumbnail", videoThumbnail)
        }



    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            videoUploadResult(intent)
        }
    }

    val videos = Pager(PagingConfig(10)) {
        VideosPagingSource()
    }.flow.cachedIn(viewModelScope)

    fun onShowDetailsClick() {
        showDetails.value = !showDetails.value!!
    }


    fun onUploadClick() {
        when {
            videoTitle.isBlank() -> {
                viewModelScope.launch {
                    videoEventChannel.send(VideoEvent.UploadVideoError("Please give a title"))
                }
            }
            videoUrl == null -> {
                viewModelScope.launch {
                    videoEventChannel.send(VideoEvent.UploadVideoError("Please select a video"))
                }
            }
            videoThumbnail == null -> {
                viewModelScope.launch {
                    videoEventChannel.send(VideoEvent.UploadVideoError("Please select a thumbnail"))
                }
            }
            else -> {
                val video = FirebaseVideo(
                    id = null,
                    title = videoTitle,
                    text = videoText,
                    time = System.currentTimeMillis(),
                    username = MainActivity.userName,
                    pfp = MainActivity.userPfp,
                    url = videoUrl.toString(),
                    thumbnail = videoThumbnail.toString()
                )
                app.startService(
                    Intent(app, FirebaseUploadService::class.java)
                        .putExtra(FirebaseUploadService.FIREBASE_VIDEO, video)
                        .putExtra("path", "video")
                        .setAction(FirebaseUploadService.ACTION_UPLOAD)
                )
            }
        }
    }

    private fun videoUploadResult(intent: Intent) {
        viewModelScope.launch {
            videoUrl = intent.getParcelableExtra(FirebaseUploadService.EXTRA_DOWNLOAD_URL)
            if (videoUrl == null) {
                videoEventChannel.send(VideoEvent.UploadVideoError("Aw snap, an error occurred"))
            } else {
                videoEventChannel.send(VideoEvent.UploadVideoSuccess("Posted Successfully"))
            }
        }
    }

    sealed class VideoEvent {
        data class UploadVideoSuccess(val message: String) : VideoEvent()
        data class UploadVideoError(val message: String) : VideoEvent()
    }

}