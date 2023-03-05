package com.example.isthisahangout.adapter.chat

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.MessagesDisplayLayoutBinding
import com.example.isthisahangout.models.FirebaseMessage
import com.example.isthisahangout.utils.firebaseAuth

class MessagesViewHolder(private val binding: MessagesDisplayLayoutBinding) :
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