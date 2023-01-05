package com.example.isthisahangout.cache.videos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.LikedVideoId
import kotlinx.coroutines.flow.Flow

@Dao
interface VideosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(likedVideoId: LikedVideoId)

    @Query("SELECT * FROM liked_videos_ids WHERE userId = :userId")
    fun getLikedVideos(userId: String): Flow<List<LikedVideoId>>

    @Query("DELETE FROM liked_videos_ids WHERE videoId = :videoId AND userId = :userId")
    suspend fun deleteLikedVideo(videoId: String, userId: String)

}