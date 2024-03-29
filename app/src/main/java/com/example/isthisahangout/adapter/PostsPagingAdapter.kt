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
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.PostDisplayLayoutBinding
import com.example.isthisahangout.models.FirebasePost
import java.text.DateFormat

class PostsPagingAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<FirebasePost, PostsPagingAdapter.PostPagingViewHolder>(
        COMPARATOR
    ) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<FirebasePost>() {
            override fun areItemsTheSame(oldItem: FirebasePost, newItem: FirebasePost) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FirebasePost, newItem: FirebasePost) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostPagingViewHolder =
        PostPagingViewHolder(
            PostDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PostPagingViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bind(item)
    }

    interface OnItemClickListener {
        fun onItemClickPaged(post: FirebasePost)
    }


    inner class PostPagingViewHolder(private val binding: PostDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClickPaged(item)
                    }
                }
            }
        }

        fun bind(post: FirebasePost) {
            binding.apply {
                Glide.with(itemView)
                    .load(post.pfp)
                    .placeholder(R.drawable.click_to_add_image)
                    .into(posterPfpImageView)
                posterUsername.text = post.username
                postTitleTextView.text = post.title
                postBody.text = post.text
                if (post.image != null) {
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
                        })
                        .into(postImageView)
                }
                timeTextView.text = DateFormat.getDateTimeInstance().format(post.time).dropLast(3)
            }
        }
    }
}