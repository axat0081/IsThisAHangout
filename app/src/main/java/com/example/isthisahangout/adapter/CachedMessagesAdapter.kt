package com.example.isthisahangout.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.MessagesDisplayLayoutBinding
import com.example.isthisahangout.models.Message

class CachedMessagesAdapter(private val id: String) :
    ListAdapter<Message, CachedMessagesAdapter.CachedMessagesViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem

            override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CachedMessagesViewHolder =
        CachedMessagesViewHolder(
            MessagesDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CachedMessagesViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class CachedMessagesViewHolder(private val binding: MessagesDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(message: Message) {
            binding.apply {
                messageSentTextView.isVisible = false
                messageReceivedTextView.text =
                    message.username + "\n" + message.text + "\n" + android.text.format.DateFormat.format(
                        "yyyy-MM-dd hh:mm:ss a",
                        message.time
                    ).toString()
            }
        }
    }
}