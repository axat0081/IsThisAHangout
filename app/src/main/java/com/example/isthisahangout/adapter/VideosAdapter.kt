package com.example.isthisahangout.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.databinding.VideoDisplayLayoutBinding
import com.example.isthisahangout.models.FirebaseVideo

class VideosPagingAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<FirebaseVideo, VideosPagingAdapter.VideosViewHolder>(COMPARATOR) {
    lateinit var viewHolder: VideosViewHolder

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<FirebaseVideo>() {
            override fun areItemsTheSame(oldItem: FirebaseVideo, newItem: FirebaseVideo): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: FirebaseVideo,
                newItem: FirebaseVideo
            ): Boolean =
                oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        viewHolder = VideosViewHolder(
            VideoDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        return viewHolder
    }


    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(video: FirebaseVideo)
    }

    inner class VideosViewHolder(val binding: VideoDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(video: FirebaseVideo) {
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