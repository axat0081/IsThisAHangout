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
import com.example.isthisahangout.databinding.GamesDisplayLayoutBinding
import com.example.isthisahangout.models.favourites.FavGame

class FavGameAdapter(private val listener: OnItemClickListener) :
    ListAdapter<FavGame, FavGameAdapter.FavGameViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<FavGame>() {
            override fun areItemsTheSame(oldItem: FavGame, newItem: FavGame) = oldItem == newItem

            override fun areContentsTheSame(oldItem: FavGame, newItem: FavGame) =
                oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavGameViewHolder =
        FavGameViewHolder(
            GamesDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: FavGameViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(anime: FavGame)
    }

    inner class FavGameViewHolder(private val binding: GamesDisplayLayoutBinding) :
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

        fun bind(game: FavGame) {
            binding.apply {
                Glide.with(itemView)
                    .load(game.image)
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
                gameTitleTextView.text = game.title
            }
        }
    }
}