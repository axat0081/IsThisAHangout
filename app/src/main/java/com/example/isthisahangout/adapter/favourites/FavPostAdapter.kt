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
import com.example.isthisahangout.databinding.PostDisplayLayoutBinding
import com.example.isthisahangout.models.favourites.FavPost

class FavPostAdapter(private val listener: OnItemClickListener) :
    ListAdapter<FavPost, FavPostAdapter.FavPostViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<FavPost>() {
            override fun areItemsTheSame(oldItem: FavPost, newItem: FavPost) = oldItem == newItem

            override fun areContentsTheSame(oldItem: FavPost, newItem: FavPost) =
                oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavPostViewHolder =
        FavPostViewHolder(
            PostDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: FavPostViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(anime: FavPost)
    }

    inner class FavPostViewHolder(private val binding: PostDisplayLayoutBinding) :
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

        fun bind(post: FavPost) {
            binding.apply {
                Glide.with(itemView)
                    .load(post.image)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            postImageProgressBar.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            postImageProgressBar.isVisible = false
                            return false
                        }
                    }).into(postImageView)
                postTitleTextView.text = post.title
            }
        }
    }
}