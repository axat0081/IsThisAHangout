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
import com.example.isthisahangout.databinding.AnimeDisplayLayoutBinding
import com.example.isthisahangout.models.RoomAnimeByDay

class AnimeByDayAdapter(private val listener: OnItemClickListener) :
    ListAdapter<RoomAnimeByDay, AnimeByDayAdapter.AnimeByDayViewHolder>(
        COMPARATOR
    ) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<RoomAnimeByDay>() {
            override fun areItemsTheSame(
                oldItem: RoomAnimeByDay,
                newItem: RoomAnimeByDay
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: RoomAnimeByDay,
                newItem: RoomAnimeByDay
            ): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeByDayViewHolder =
        AnimeByDayViewHolder(
            AnimeDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AnimeByDayViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    interface OnItemClickListener {
        fun onItemClick(anime: RoomAnimeByDay)
    }

    inner class AnimeByDayViewHolder(private val binding: AnimeDisplayLayoutBinding) :
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

        fun bind(anime: RoomAnimeByDay) {
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