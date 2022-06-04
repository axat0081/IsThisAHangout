package com.example.isthisahangout.adapter

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
import com.example.isthisahangout.databinding.AnimeDisplayLayoutBigBinding
import com.example.isthisahangout.models.AnimeByNameResults

class AnimeByNameAdapter(private val listener: OnItemClickListener) :
    ListAdapter<AnimeByNameResults.AnimeByName, AnimeByNameAdapter.AnimeByNameViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<AnimeByNameResults.AnimeByName>() {
            override fun areItemsTheSame(
                oldItem: AnimeByNameResults.AnimeByName,
                newItem: AnimeByNameResults.AnimeByName
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: AnimeByNameResults.AnimeByName,
                newItem: AnimeByNameResults.AnimeByName
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeByNameViewHolder =
        AnimeByNameViewHolder(
            AnimeDisplayLayoutBigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AnimeByNameViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    interface OnItemClickListener {
        fun onItemClick(anime: AnimeByNameResults.AnimeByName)
    }

    inner class AnimeByNameViewHolder(private val binding: AnimeDisplayLayoutBigBinding) :
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

        fun bind(anime: AnimeByNameResults.AnimeByName) {
            binding.apply {
                Glide.with(itemView)
                    .load(anime.imageUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            animeProgressBarBig.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            animeProgressBarBig.isVisible = false
                            return false
                        }
                    }).into(animeImageViewBig)
                animeTitleTextView.text = anime.title
            }
        }
    }
}