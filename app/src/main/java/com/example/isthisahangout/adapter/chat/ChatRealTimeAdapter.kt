package com.example.isthisahangout.adapter.chat

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.isthisahangout.databinding.MessagesDisplayLayoutBinding
import com.example.isthisahangout.models.FirebaseMessage

class ChatRealTimeAdapter :
    ListAdapter<FirebaseMessage, MessagesViewHolder>(
        MESSAGE_COMPARATOR
    ) {

    companion object {
        val MESSAGE_COMPARATOR = object : DiffUtil.ItemCallback<FirebaseMessage>() {
            override fun areItemsTheSame(
                oldItem: FirebaseMessage,
                newItem: FirebaseMessage,
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: FirebaseMessage,
                newItem: FirebaseMessage,
            ): Boolean =
                oldItem == newItem

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder =
        MessagesViewHolder(
            MessagesDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

}