package com.example.isthisahangout.adapter.favourites

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.databinding.VideoDisplayLayoutBinding
import com.example.isthisahangout.models.favourites.FavVideo

class FavVideosAdapter(private val listener: OnItemClickListener) :
    ListAdapter<FavVideo, FavVideosAdapter.FavVideosViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<FavVideo>() {
            override fun areItemsTheSame(oldItem: FavVideo, newItem: FavVideo) = oldItem == newItem

            override fun areContentsTheSame(oldItem: FavVideo, newItem: FavVideo) =
                oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavVideosViewHolder =
        FavVideosViewHolder(
            VideoDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: FavVideosViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(anime: FavVideo)
    }

    inner class FavVideosViewHolder(private val binding: VideoDisplayLayoutBinding) :
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
        fun bind(video: FavVideo) {
            binding.apply {
                Glide.with(itemView)
                    .load(video.thumbnail)
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