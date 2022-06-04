package com.example.isthisahangout.adapter.chat

import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.google.firebase.firestore.*


enum class LoadingState {
    EMPTY,
    LOADING_INITIAL,
    INITIAL_LOADED,
    LOADING_MORE,
    MORE_LOADED,
    FINISHED,
    ERROR,
    NEW_ITEM,
    DELETED_ITEM
}


abstract class FirestoreRealTimePaginationAdapter<T, VH : RecyclerView.ViewHolder>(
    paginationQuery: Query,
    realTimeQuery: Query,
    val parser: (DocumentSnapshot) -> T?,
    val prefetchDistance: Int,
    val pageSize: Int
) : RecyclerView.Adapter<VH>(), LifecycleObserver {
    abstract val data: SortedList<T>
    val loadingState = MutableLiveData<LoadingState>()

    private val dataSource = FirestorePaginationDataSource(paginationQuery)

    private var newMessagesListenerRegistration: ListenerRegistration? = null

    fun startListening() {
        loadInitial()
    }

    fun stopListening() {
    }

    fun cleanup() {
        newMessagesListenerRegistration?.remove()
    }

    init {

        newMessagesListenerRegistration = realTimeQuery
            .addSnapshotListener { snapshots: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    loadingState.postValue(LoadingState.ERROR)
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty)
                    for (documentChange in snapshots.documentChanges) {
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                val item = parser(documentChange.document)
                                data.add(item)
                                loadingState.postValue(LoadingState.NEW_ITEM)
                                notifyDataSetChanged()
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
        loadingState.postValue(LoadingState.LOADING_INITIAL)
        dataSource.loadInitial(
            pageSize
        ) { querySnapshot: QuerySnapshot ->
            data.addAll(querySnapshot.documents.map(parser))
            loadingState.value = if (querySnapshot.isEmpty)
                LoadingState.EMPTY
            else
                LoadingState.INITIAL_LOADED
        }
    }

    private fun loadMore() {
        if (dataSource.canLoadMore()) {
            loadingState.postValue(LoadingState.LOADING_MORE)

            dataSource.loadMore(
                pageSize
            ) { querySnapshot: QuerySnapshot ->
                if (querySnapshot.documents.isEmpty()) {
                    loadingState.postValue(LoadingState.FINISHED)
                } else {
                    data.addAll(querySnapshot.documents.map(parser))
                    loadingState.postValue(LoadingState.MORE_LOADED)
                }
            }
        }
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position - prefetchDistance == 0) {
            loadMore()
        }
    }

    override fun getItemCount(): Int = data.size()
}