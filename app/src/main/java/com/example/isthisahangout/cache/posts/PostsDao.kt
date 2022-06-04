package com.example.isthisahangout.cache.posts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.LikedPostId
import kotlinx.coroutines.flow.Flow

@Dao
interface PostsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedPostId(id: LikedPostId)

    @Query("SELECT * FROM liked_posts_ids WHERE userId = :userId AND postId = :postId")
    fun getLikesPostsId(userId: String, postId: String): Flow<LikedPostId?>

    @Query("DELETE FROM liked_posts_ids WHERE postId = :id AND userId = :userId")
    suspend fun deleteLikedPostId(id: String, userId: String)
}