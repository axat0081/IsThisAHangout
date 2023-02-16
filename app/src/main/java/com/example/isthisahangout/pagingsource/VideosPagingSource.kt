package com.example.isthisahangout.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.isthisahangout.models.FirebaseVideo
import com.example.isthisahangout.utils.videoQuery
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class VideosPagingSource : PagingSource<QuerySnapshot, FirebaseVideo>() {
    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, FirebaseVideo> {
        return try {
            val currentPage = params.key ?: videoQuery.limit(10).get().await()
            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
            val nextPage = videoQuery.limit(10).startAfter(lastDocumentSnapshot)
                .get().await()
            LoadResult.Page(
                data = currentPage.toObjects(FirebaseVideo::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, FirebaseVideo>): QuerySnapshot? {
        return null
    }
}