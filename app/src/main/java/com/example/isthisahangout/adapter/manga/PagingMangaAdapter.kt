package com.example.isthisahangout.adapter.manga

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
import com.example.isthisahangout.databinding.AnimeDisplayLayoutBinding
import com.example.isthisahangout.databinding.SeparatorLayoutBinding
import com.example.isthisahangout.ui.models.MangaUIModel

class MangaPagingAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<MangaUIModel, RecyclerView.ViewHolder>(
        COMPARATOR
    ) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<MangaUIModel>() {
            override fun areItemsTheSame(
                oldItem: MangaUIModel,
                newItem: MangaUIModel
            ): Boolean {
                return (oldItem is MangaUIModel.MangaModel && newItem is MangaUIModel.MangaModel &&
                        oldItem.title == newItem.title) ||
                        (oldItem is MangaUIModel.MangaSeparator && newItem is MangaUIModel.MangaSeparator &&
                                oldItem.desc == newItem.desc)
            }

            override fun areContentsTheSame(
                oldItem: MangaUIModel,
                newItem: MangaUIModel
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
        else -> SeparatorViewHolder(
            SeparatorLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return when (peek(position)) {
            is MangaUIModel.MangaModel -> R.layout.anime_display_layout
            is MangaUIModel.MangaSeparator -> R.layout.separator_layout
            null -> throw IllegalStateException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            when (holder) {
                is PaginatedAnimeViewHolder -> holder.bind(item as MangaUIModel.MangaModel)
                is SeparatorViewHolder -> holder.bind((item as MangaUIModel.MangaSeparator).desc)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(mangaResults: MangaUIModel.MangaModel)
    }


    inner class PaginatedAnimeViewHolder(val binding: AnimeDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null && item is MangaUIModel.MangaModel) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(mangaResults: MangaUIModel.MangaModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(mangaResults.imageUrl)
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
                animeTitleTextView.text = mangaResults.title
            }
        }
    }
}