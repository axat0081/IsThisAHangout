package com.example.isthisahangout.cache.favourites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.favourites.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: FavAnime)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(manga: FavManga)

    @Query("SELECT * FROM anime_favourites WHERE userId = :userId AND title LIKE '%' || :searchQuery || '%'")
    fun getAnime(searchQuery: String, userId: String): Flow<List<FavAnime>>

    @Query("SELECT * FROM manga_favourites WHERE userId = :userId AND title LIKE '%' || :searchQuery || '%'")
    fun getManga(userId: String, searchQuery: String): Flow<List<FavAnime>>

    @Query("DELETE FROM anime_favourites WHERE id = :id AND userId = :userId")
    suspend fun deleteAnime(id: Int, userId: String)

    @Query("DELETE FROM manga_favourites WHERE id = :mangaId AND userId = :userId")
    suspend fun deleteManga(mangaId: String, userId: String)

    @Query("DELETE FROM anime_favourites WHERE id = :animeId AND userId = :userId")
    suspend fun deleteAnimeByName(animeId: String, userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: FavGame)

    @Query("SELECT * FROM game_favourites WHERE userId = :userId AND title LIKE '%' || :searchQuery || '%'")
    fun getGames(searchQuery: String, userId: String): Flow<List<FavGame>>

    @Query("DELETE FROM game_favourites WHERE id = :id AND userId =:userId")
    suspend fun deleteGame(id: Int, userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: FavVideo)

    @Query("SELECT * FROM video_favourites WHERE userId = :userId AND title LIKE '%' || :searchQuery || '%'")
    fun getVideos(searchQuery: String, userId: String): Flow<List<FavVideo>>

    @Query("DELETE FROM video_favourites WHERE id = :id AND userId =:userId")
    suspend fun deleteVideo(id: String, userId: String)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: FavPost)

    @Query("SELECT * FROM post_favourites WHERE userId = :userId AND title LIKE '%' || :searchQuery || '%'")
    fun getPosts(searchQuery: String, userId: String): Flow<List<FavPost>>

    @Query("DELETE FROM post_favourites WHERE id = :id AND userId =:userId")
    suspend fun deletePost(id: String, userId: String)

}