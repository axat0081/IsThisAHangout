package com.example.isthisahangout.cache.movies

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.movies.MovieEntity
import com.example.isthisahangout.models.movies.MoviesRemoteKey

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMovies(movieEntities: List<MovieEntity>)

    @Query("SELECT * FROM movie")
    fun getMoviesPaged(): PagingSource<Int, MovieEntity>

    @Query("SELECT MAX(updatedAt) FROM movie")
    suspend fun getLastUpdatedAt(): Long?

    @Query("DELETE FROM movie")
    suspend fun deleteAllMovies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKey(moviesRemoteKey: MoviesRemoteKey)

    @Query("SELECT * FROM movies_remote_key")
    suspend fun getMoviesRemoteKey(): MoviesRemoteKey?

    @Query("DELETE FROM movies_remote_key")
    suspend fun deleteRemoteKey()

    suspend fun updateRemoteKey(moviesRemoteKey: MoviesRemoteKey) {
        deleteRemoteKey()
        insertRemoteKey(moviesRemoteKey)
    }
}