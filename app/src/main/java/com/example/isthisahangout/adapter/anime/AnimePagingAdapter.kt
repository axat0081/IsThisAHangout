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
import com.example.isthisahangout.adapter.SeparatorViewHolder
import com.example.isthisahangout.databinding.AnimeDisplayLayoutBinding
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
                                oldItem.desc == newItem.desc)
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
    ): PaginatedAnimeViewHolder {
        return PaginatedAnimeViewHolder(
            AnimeDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            when (item) {
                is AnimeUIModel.AnimeModel -> (holder as PaginatedAnimeViewHolder).bind(item)
                is AnimeUIModel.AnimeSeparator -> (holder as SeparatorViewHolder).bind(item.desc)
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