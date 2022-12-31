package com.example.isthisahangout.adapter.anime

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.AnimeSeparatorViewHolder
import com.example.isthisahangout.databinding.AnimeDisplayLayoutBinding
import com.example.isthisahangout.databinding.AnimeSeparatorLayoutBinding
import com.example.isthisahangout.ui.models.AnimeUIModel

class AnimePagingAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<AnimeUIModel, ViewHolder>(
        COMPARATOR
    ) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<AnimeUIModel>() {
            override fun areItemsTheSame(
                oldItem: AnimeUIModel,
                newItem: AnimeUIModel
            ): Boolean {
                return (oldItem is AnimeUIModel.AnimeModel && newItem is AnimeUIModel.AnimeModel &&
                        oldItem.title == newItem.title) ||
                        (oldItem is AnimeUIModel.AnimeSeparator && newItem is AnimeUIModel.AnimeSeparator &&
                                oldItem.favorites == newItem.favorites)
            }

            override fun areContentsTheSame(
                oldItem: AnimeUIModel,
                newItem: AnimeUIModel
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (viewType) {
        R.layout.anime_display_layout -> PaginatedAnimeViewHolder(
            AnimeDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        else -> AnimeSeparatorViewHolder(
            AnimeSeparatorLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return when (peek(position)) {
            is AnimeUIModel.AnimeModel -> R.layout.anime_display_layout
            is AnimeUIModel.AnimeSeparator -> R.layout.video_game_separator_layout
            else -> R.layout.anime_display_layout
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            when (holder) {
                is PaginatedAnimeViewHolder -> holder.bind(item as AnimeUIModel.AnimeModel)
                is AnimeSeparatorViewHolder -> holder.bind((item as AnimeUIModel.AnimeSeparator).favorites)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(animeResults: AnimeUIModel.AnimeModel)
    }


    inner class PaginatedAnimeViewHolder(val binding: AnimeDisplayLayoutBinding) :
        ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null && item is AnimeUIModel.AnimeModel) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(animeResults: AnimeUIModel.AnimeModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(animeResults.imageUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            animeProgressBar.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            animeProgressBar.isVisible = false
                            return false
                        }
                    }).into(animeImageView)
                animeTitleTextView.text = animeResults.title
            }
        }
    }
}