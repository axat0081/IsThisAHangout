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
import com.example.isthisahangout.databinding.AnimeDisplayLayoutBinding
import com.example.isthisahangout.models.UpcomingAnimeResponse

class UpcomingAnimeAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<UpcomingAnimeResponse.UpcomingAnime, UpcomingAnimeAdapter.UpcomingAnimeViewHolder>(
        COMPARATOR
    ) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<UpcomingAnimeResponse.UpcomingAnime>() {
            override fun areItemsTheSame(
                oldItem: UpcomingAnimeResponse.UpcomingAnime,
                newItem: UpcomingAnimeResponse.UpcomingAnime
            ) = oldItem.title == newItem.title

            override fun areContentsTheSame(
                oldItem: UpcomingAnimeResponse.UpcomingAnime,
                newItem: UpcomingAnimeResponse.UpcomingAnime
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpcomingAnimeViewHolder {
        return UpcomingAnimeViewHolder(
            AnimeDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: UpcomingAnimeViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bind(item)
    }

    interface OnItemClickListener {
        fun onItemClick(animeResults: UpcomingAnimeResponse.UpcomingAnime)
    }

    inner class UpcomingAnimeViewHolder(val binding: AnimeDisplayLayoutBinding) :
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

        fun bind(animeResults: UpcomingAnimeResponse.UpcomingAnime) {
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