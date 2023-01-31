package com.example.isthisahangout.adapter.openAI

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.isthisahangout.databinding.CommentsDisplayLayoutBinding
import com.example.isthisahangout.models.openAI.OpenAIMessage
import java.text.DateFormat

class OpenAIChatAdapter :
    ListAdapter<OpenAIMessage, OpenAIChatAdapter.OpenAIViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<OpenAIMessage>() {
            override fun areItemsTheSame(oldItem: OpenAIMessage, newItem: OpenAIMessage): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: OpenAIMessage,
                newItem: OpenAIMessage,
            ): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenAIViewHolder =
        OpenAIViewHolder(
            CommentsDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OpenAIViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class OpenAIViewHolder(private val binding: CommentsDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(openAIMessage: OpenAIMessage) {
            binding.apply {
                Glide.with(itemView)
                    .load(openAIMessage.pfp)
                    .into(commenterPfpImageView)
                commenterUsername.text = openAIMessage.userName
                commentTimeTextView.text =
                    DateFormat.getDateTimeInstance().format(openAIMessage.time).drop(3)
                replyingToLayout.isVisible = false
                commentImageProgressBar.isVisible = false
                commentImageView.isVisible = false
            }
        }
    }
}