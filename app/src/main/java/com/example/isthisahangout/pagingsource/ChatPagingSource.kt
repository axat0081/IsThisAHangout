package com.example.isthisahangout.pagingsource

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source


class ChatPagingSource(
    private val query: Query,
    private val mSource: Source = Source.DEFAULT
) {
    private var lastDocumentSnapshot: DocumentSnapshot? = null
    fun canLoadMore() = lastDocumentSnapshot != null

    fun loadInitial(
        loadSize: Int,
        onLoaded: (querySnapshot: QuerySnapshot) -> Unit
    ) {
        query
            .limit(loadSize.toLong())
            .get(mSource)
            .addOnSuccessListener { querySnapshot ->
                lastDocumentSnapshot = querySnapshot.documents.lastOrNull()
                onLoaded(querySnapshot)
            }
    }

    fun loadMore(
        loadSize: Int,
        onLoaded: (querySnapshot: QuerySnapshot) -> Unit
    ) {
        lastDocumentSnapshot?.let {
            query
                .startAfter(it)
                .limit(loadSize.toLong())
                .get(mSource)
                .addOnSuccessListener { querySnapshot ->
                    lastDocumentSnapshot = querySnapshot.documents.lastOrNull()
                    onLoaded(querySnapshot)
                }
        }
    }
}
