package com.example.isthisahangout.adapter.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.example.isthisahangout.databinding.MessagesDisplayLayoutBinding
import com.example.isthisahangout.models.FirebaseMessage
import com.example.isthisahangout.utils.chatMessagesQuery
import com.example.isthisahangout.utils.firebaseAuth
import com.example.isthisahangout.utils.newChatMessagesQuery
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

fun Query.whereAfterTimestamp(): Query =
    whereGreaterThan("time", Timestamp.now())

class ChatAdapter(
) : FirestoreRealTimePaginationAdapter<FirebaseMessage, ChatAdapter.ViewHolder>(
    paginationQuery = chatMessagesQuery,
    realTimeQuery = newChatMessagesQuery,
    prefetchDistance = 3,
    pageSize = 10,
    parser = { documentSnapshot ->
        documentSnapshot.toObject(FirebaseMessage::class.java)
    }
) {

    override val data: SortedList<FirebaseMessage> = SortedList(
        FirebaseMessage::class.java,
        object : SortedListAdapterCallback<FirebaseMessage>(this) {
            override fun compare(a: FirebaseMessage, b: FirebaseMessage): Int =
                a.time.compareTo(b.time)

            override fun areContentsTheSame(
                oldItem: FirebaseMessage,
                newItem: FirebaseMessage
            ): Boolean =
                oldItem.text == newItem.text

            override fun areItemsTheSame(item1: FirebaseMessage, item2: FirebaseMessage): Boolean =
                item1.id == item2.id
        })

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder =
        ViewHolder(
            MessagesDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bindData(data[position])
    }

    inner class ViewHolder(private val binding: MessagesDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindData(message: FirebaseMessage) {
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