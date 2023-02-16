package com.example.isthisahangout.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.isthisahangout.models.FirebasePost
import com.example.isthisahangout.utils.postsQuery
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class PostsPagingSource :
    PagingSource<QuerySnapshot, FirebasePost>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, FirebasePost> {
        return try {
            val currentPage = params.key ?: postsQuery.limit(10).get().await()
            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
            val nextPage = postsQuery.limit(10).startAfter(lastDocumentSnapshot)
                .get().await()
            LoadResult.Page(
                data = currentPage.toObjects(FirebasePost::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, FirebasePost>): QuerySnapshot? {
        return null
    }
}