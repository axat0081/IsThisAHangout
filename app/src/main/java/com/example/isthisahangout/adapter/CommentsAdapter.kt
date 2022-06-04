package com.example.isthisahangout.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.CommentsDisplayLayoutBinding
import com.example.isthisahangout.models.Comments
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.text.DateFormat

class CommentsAdapter(
    options: FirestoreRecyclerOptions<Comments>,
    private val listener: OnItemLongClickListener
) :
    FirestoreRecyclerAdapter<Comments, CommentsAdapter.CommentsViewHolder>(
        options
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder =
        CommentsViewHolder(
            CommentsDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int, model: Comments) {
        val item = getItem(position)
        holder.bind(item)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(comment: Comments)
    }

    inner class CommentsViewHolder(private val binding: CommentsDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnLongClickListener {
              listener.onItemLongClick(getItem(bindingAdapterPosition))
              true
            }
        }

        fun bind(comment: Comments) {
            binding.apply {
                Glide.with(itemView)
                    .load(comment.pfp)
                    .placeholder(R.drawable.click_to_add_image)
                    .into(commenterPfpImageView)
                commentTextTextView.text = comment.text
                commenterUsername.text = comment.username
                commentTimeTextView.text = DateFormat.getDateTimeInstance().format(comment.time)
                if (comment.image == null) {
                    commentImageView.isVisible = false
                    commentImageProgressBar.isVisible = false

                } else {
                    Glide.with(itemView)
                        .load(comment.image)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                commentImageProgressBar.isVisible = false
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                commentImageProgressBar.isVisible = false
                                return false
                            }
                        }).into(commentImageView)
                }
            }
        }
    }
}