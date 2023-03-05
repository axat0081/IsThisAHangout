package com.example.isthisahangout.adapter.chat

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.MessagesDisplayLayoutBinding
import com.example.isthisahangout.models.FirebaseMessage
import com.example.isthisahangout.utils.firebaseAuth
import java.util.*

class ChatPagingAdapter :
    PagingDataAdapter<FirebaseMessage, ChatPagingAdapter.FirebaseMessageViewHolder>(
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirebaseMessageViewHolder =
        FirebaseMessageViewHolder(
            MessagesDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FirebaseMessageViewHolder, position: Int) {
        val message = getItem(position)
        if (message != null)
            holder.bind(message)
    }

    inner class FirebaseMessageViewHolder(private val binding: MessagesDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(message: FirebaseMessage) {
            binding.apply {
                if (message.senderId == firebaseAuth.currentUser!!.uid) {
                    linearLayout1.isVisible = false
                    sentByTextView.text = message.username
                    messageSentTextView.text =
                        message.text + "\n\n" + android.text.format.DateFormat.format(
                            "yyyy-MM-dd hh:mm a",
                            message.time.toDate()
                        ).toString()
                } else {
                    linearLayout2.isVisible = false
                    usernameTextView.text = message.username
                    messageReceivedTextView.text =
                        message.text + "\n\n" + android.text.format.DateFormat.format(
                            "yyyy-MM-dd hh:mm a",
                            message.time.toDate()
                        ).toString()
                }
            }
        }
    }
}