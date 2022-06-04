package com.example.isthisahangout.adapter

import android.annotation.SuppressLint
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
import com.example.isthisahangout.databinding.AnimeNewsDisplayLayoutBinding
import com.example.isthisahangout.models.AnimeNews

class AnimeNewsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<AnimeNews, AnimeNewsAdapter.AnimeNewsViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<AnimeNews>() {
            override fun areItemsTheSame(oldItem: AnimeNews, newItem: AnimeNews): Boolean =
                oldItem.url == newItem.url

            override fun areContentsTheSame(oldItem: AnimeNews, newItem: AnimeNews): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeNewsViewHolder =
        AnimeNewsViewHolder(
            AnimeNewsDisplayLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AnimeNewsViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(animeNews: AnimeNews)
    }

    inner class AnimeNewsViewHolder(private val binding: AnimeNewsDisplayLayoutBinding) :
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

        @SuppressLint("SetTextI18n")
        fun bind(animeNews: AnimeNews) {
            binding.apply {
                Glide.with(itemView)
                    .load(animeNews.image)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            animeNewsProgressBar.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            animeNewsProgressBar.isVisible = false
                            return false
                        }
                    }).into(animeNewsImageView)
                animeNewsTitleTextView.text = animeNews.title
            }
        }
    }
}