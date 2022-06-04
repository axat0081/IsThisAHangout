package com.example.isthisahangout.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.databinding.VideoDisplayLayoutBinding
import com.example.isthisahangout.models.FirebaseVideo
import com.example.isthisahangout.utils.newVideoQuery
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class VideosRecyclerAdapter(private val listener: OnItemClickListener) :
    FirestoreRecyclerAdapter<FirebaseVideo, VideosRecyclerAdapter.VideosViewHolder>(options) {

    companion object {
        var options: FirestoreRecyclerOptions<FirebaseVideo> =
            FirestoreRecyclerOptions.Builder<FirebaseVideo>()
                .setQuery(
                    newVideoQuery,
                    FirebaseVideo::class.java
                )
                .build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder =
        VideosViewHolder(
            VideoDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int, model: FirebaseVideo) {
        val item = getItem(position)
        holder.bind(item)
    }

    interface OnItemClickListener {
        fun onItemClick(post: FirebaseVideo)
    }

    inner class VideosViewHolder(private val binding: VideoDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    listener.onItemClick(item)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(video: FirebaseVideo) {
            binding.apply {
                binding.apply {
                    Glide.with(itemView)
                        .load(video.thumbnail)
                        .apply(object : RequestOptions() {}.override(600, 200))
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                videoThumbnailProgressBar.isVisible = false
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                videoThumbnailProgressBar.isVisible = false
                                return false
                            }
                        }).into(videoThumbnailImageView)
                    Glide.with(itemView)
                        .load(video.pfp)
                        .into(videoUploaderPfpImageview)
                    videoTitleTextview.text = video.title
                    uploaderUsernameTextView.text = "Uploaded by - ${video.username}"
                }

            }
        }
    }
}