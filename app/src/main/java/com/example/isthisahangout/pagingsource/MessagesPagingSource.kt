package com.example.isthisahangout.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.isthisahangout.models.FirebaseMessage
import com.example.isthisahangout.utils.messagesQuery
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class MessagesPagingSource :
    PagingSource<QuerySnapshot, FirebaseMessage>() {
    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, FirebaseMessage> {
        return try {
            val currentPage = params.key ?: messagesQuery.limit(10).get().await()
            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
            val nextPage = messagesQuery.limit(5).startAfter(lastDocumentSnapshot)
                .get().await()
            LoadResult.Page(
                data = currentPage.toObjects(FirebaseMessage::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, FirebaseMessage>): QuerySnapshot? {
        return null
    }
}