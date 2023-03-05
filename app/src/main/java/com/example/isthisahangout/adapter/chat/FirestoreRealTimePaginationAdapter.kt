package com.example.isthisahangout.adapter.chat

import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.google.firebase.firestore.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


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
    private val _loadingState = MutableStateFlow(LoadingState.LOADING_INITIAL)
    val loadingState = _loadingState.asStateFlow()
    private val _itemCount = MutableStateFlow(0)
    val itemCount = _itemCount.asStateFlow()
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
                    _loadingState.value = LoadingState.ERROR
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty)
                    for (documentChange in snapshots.documentChanges) {
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                val item = parser(documentChange.document)
                                data.add(item)
                                _loadingState.value = LoadingState.NEW_ITEM
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
        _loadingState.value = LoadingState.LOADING_INITIAL
        dataSource.loadInitial(
            pageSize
        ) { querySnapshot: QuerySnapshot ->
            data.addAll(querySnapshot.documents.map(parser))
            _itemCount.value = data.size()
            _loadingState.value = if (querySnapshot.isEmpty)
                LoadingState.EMPTY
            else
                LoadingState.INITIAL_LOADED
        }
    }

    private fun loadMore() {
        if (dataSource.canLoadMore()) {
            _loadingState.value = LoadingState.LOADING_MORE
            dataSource.loadMore(
                pageSize
            ) { querySnapshot: QuerySnapshot ->
                if (querySnapshot.documents.isEmpty()) {
                    _loadingState.value = LoadingState.FINISHED
                } else {
                    data.addAll(querySnapshot.documents.map(parser))
                    _itemCount.value = data.size()
                    _loadingState.value = LoadingState.MORE_LOADED
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