package com.example.isthisahangout.adapter.videoGame

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
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.SeparatorViewHolder
import com.example.isthisahangout.databinding.GamesDisplayLayoutBinding
import com.example.isthisahangout.databinding.SeparatorLayoutBinding
import com.example.isthisahangout.ui.models.VideoGameUIModel

class VideoGamesPagingAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<VideoGameUIModel, RecyclerView.ViewHolder>(
        COMPARATOR
    ) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<VideoGameUIModel>() {
            override fun areItemsTheSame(
                oldItem: VideoGameUIModel,
                newItem: VideoGameUIModel
            ): Boolean {
                return (oldItem is VideoGameUIModel.VideoGameModel && newItem is VideoGameUIModel.VideoGameModel &&
                        oldItem.id == newItem.id) ||
                        (oldItem is VideoGameUIModel.VideoGameSeparator && newItem is VideoGameUIModel.VideoGameSeparator &&
                                oldItem.desc == newItem.desc)
            }

            override fun areContentsTheSame(
                oldItem: VideoGameUIModel,
                newItem: VideoGameUIModel
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (viewType) {
        R.layout.games_display_layout -> PaginatedVideoGameViewHolder(
            GamesDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        else -> SeparatorViewHolder(
            SeparatorLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return when (peek(position)) {
            is VideoGameUIModel.VideoGameModel -> R.layout.games_display_layout
            is VideoGameUIModel.VideoGameSeparator -> R.layout.separator_layout
            null -> throw IllegalStateException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            when(holder) {
                is PaginatedVideoGameViewHolder -> holder.bind(item as VideoGameUIModel.VideoGameModel)
                is SeparatorViewHolder -> holder.bind((item as VideoGameUIModel.VideoGameSeparator).desc)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(videoGame: VideoGameUIModel.VideoGameModel)
    }


    inner class PaginatedVideoGameViewHolder(val binding: GamesDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null && item is VideoGameUIModel.VideoGameModel) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(game: VideoGameUIModel.VideoGameModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(game.imageUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            gameProgressBar.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            gameProgressBar.isVisible = false
                            return false
                        }
                    }).into(gameImageView)
                gameTitleTextView.text = game.name
            }
        }
    }
}