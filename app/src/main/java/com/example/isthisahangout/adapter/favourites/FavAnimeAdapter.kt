package com.example.isthisahangout.adapter.favourites

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
import com.example.isthisahangout.databinding.AnimeDisplayLayoutBinding
import com.example.isthisahangout.models.favourites.FavAnime

class FavAnimeAdapter(private val listener: OnItemClickListener) :
    ListAdapter<FavAnime, FavAnimeAdapter.FavAnimeViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<FavAnime>() {
            override fun areItemsTheSame(oldItem: FavAnime, newItem: FavAnime) = oldItem == newItem

            override fun areContentsTheSame(oldItem: FavAnime, newItem: FavAnime) =
                oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavAnimeViewHolder =
        FavAnimeViewHolder(
            AnimeDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: FavAnimeViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(anime: FavAnime)
    }

    inner class FavAnimeViewHolder(private val binding: AnimeDisplayLayoutBinding) :
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

        fun bind(anime: FavAnime) {
            binding.apply {
                Glide.with(itemView)
                    .load(anime.image)
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
                animeTitleTextView.text = anime.title
            }
        }
    }
}