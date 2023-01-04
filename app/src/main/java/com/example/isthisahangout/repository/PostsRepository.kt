package com.example.isthisahangout.repository

import com.example.isthisahangout.models.Comments
import com.example.isthisahangout.models.FirebasePost
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.utils.asFlow
import com.example.isthisahangout.utils.asResourceFlow
import com.example.isthisahangout.utils.newPostsQuery
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PostsRepository @Inject constructor(
    @Named("CommentsRef") val commentsRef: CollectionReference
) {

    fun getNewlyUpdatedPosts(): Flow<List<FirebasePost>> =
        newPostsQuery.asFlow().map { snapshot ->
            snapshot.toObjects(FirebasePost::class.java)
        }

    fun getPostComments(postId: String?): Flow<Resource<List<Comments>>> =
        postId?.let {
            commentsRef.document(postId).collection("comments").asResourceFlow {
                it.toObjects(Comments::class.java)
            }
        } ?: emptyFlow()

}