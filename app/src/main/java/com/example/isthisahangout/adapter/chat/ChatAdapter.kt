package com.example.isthisahangout.adapter.chat

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.recyclerview.widget.*
import com.example.isthisahangout.databinding.MessagesDisplayLayoutBinding
import com.example.isthisahangout.models.FirebaseMessage
import com.example.isthisahangout.pagingsource.ChatPagingSource
import com.google.firebase.firestore.*


sealed class MessagesLoadingState {
    object Empty: MessagesLoadingState()
    object LoadingInitial: MessagesLoadingState()
    object InitialLoaded: MessagesLoadingState()
    object LoadingMore: MessagesLoadingState()
    object MoreLoaded: MessagesLoadingState()
    object Error: MessagesLoadingState()
    object Finished: MessagesLoadingState()
    data class NewItem(val message: FirebaseMessage?): MessagesLoadingState()
    data class DeletedItem(val message: FirebaseMessage?): MessagesLoadingState()
}


class ChatAdapter(
    paginationQuery: Query,
    realTimeQuery: Query,
    val parser: (DocumentSnapshot) -> FirebaseMessage?,
    private val prefetchDistance: Int,
    private val pageSize: Int,
) : ListAdapter<FirebaseMessage, MessagesViewHolder>(MESSAGES_COMPARATOR){

    companion object {
        private val MESSAGES_COMPARATOR = object : DiffUtil.ItemCallback<FirebaseMessage>() {
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

    val data: SortedList<FirebaseMessage> = SortedList(
        FirebaseMessage::class.java,
        object : SortedListAdapterCallback<FirebaseMessage>(this) {
            override fun compare(a: FirebaseMessage, b: FirebaseMessage): Int =
                a.time.compareTo(b.time)

            override fun areContentsTheSame(
                oldItem: FirebaseMessage,
                newItem: FirebaseMessage,
            ): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(item1: FirebaseMessage, item2: FirebaseMessage): Boolean =
                item1.id == item2.id
        })

    val loadingState = MutableLiveData<MessagesLoadingState>()
    private val dataSource = ChatPagingSource(paginationQuery)
    private var newMessagesListenerRegistration: ListenerRegistration? = null


    fun onStart() {
        loadInitial()
    }

    fun onDestroy() {
        newMessagesListenerRegistration?.remove()
    }


    init {
        newMessagesListenerRegistration = realTimeQuery
            .addSnapshotListener { snapshots: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    loadingState.postValue(MessagesLoadingState.Error)
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty)
                    for (documentChange in snapshots.documentChanges) {
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                val item = parser(documentChange.document)
                                val position = data.size()
                                data.add(item)
                                notifyItemInserted(position)
                                loadingState.postValue(MessagesLoadingState.NewItem(item))
                            }
                            DocumentChange.Type.REMOVED -> {
                            }
                            DocumentChange.Type.MODIFIED -> {
                            }
                        }
                    }
            }
    }

    private fun loadInitial() {
        loadingState.postValue(MessagesLoadingState.LoadingInitial)
        dataSource.loadInitial(
            pageSize
        ) { querySnapshot: QuerySnapshot ->
            data.addAll(querySnapshot.documents.map(parser))
            loadingState.value = if (querySnapshot.isEmpty)
                MessagesLoadingState.Empty
            else
                MessagesLoadingState.InitialLoaded
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder =
        MessagesViewHolder(
            MessagesDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    private fun loadMore() {
        if (dataSource.canLoadMore()) {
            loadingState.postValue(MessagesLoadingState.LoadingMore)

            dataSource.loadMore(
                pageSize
            ) { querySnapshot: QuerySnapshot ->
                if (querySnapshot.documents.isEmpty()) {
                    loadingState.value = MessagesLoadingState.Finished
                } else {
                    data.addAll(querySnapshot.documents.map(parser))
                    loadingState.value = MessagesLoadingState.MoreLoaded
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @CallSuper
    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        if (position - prefetchDistance == 0) {
            loadMore()
        }
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size()
}