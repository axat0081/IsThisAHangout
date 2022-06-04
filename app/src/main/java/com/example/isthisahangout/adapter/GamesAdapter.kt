package com.example.isthisahangout.adapter

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
import com.example.isthisahangout.databinding.GamesDisplayLayoutBinding
import com.example.isthisahangout.models.RoomGames

class GamesAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<RoomGames, GamesAdapter.GamesViewHolder>(
        COMPARATOR
    ) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<RoomGames>() {
            override fun areItemsTheSame(
                oldItem: RoomGames,
                newItem: RoomGames
            ) = oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: RoomGames,
                newItem: RoomGames
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GamesViewHolder {
        return GamesViewHolder(
            GamesDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bind(item)
    }

    interface OnItemClickListener {
        fun onItemClick(games: RoomGames)
    }

    inner class GamesViewHolder(private val binding: GamesDisplayLayoutBinding) :
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

        fun bind(game: RoomGames) {
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