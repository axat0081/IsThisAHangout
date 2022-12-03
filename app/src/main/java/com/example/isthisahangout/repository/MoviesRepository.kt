package com.example.isthisahangout.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.isthisahangout.api.MoviesAPI
import com.example.isthisahangout.cache.movies.MoviesDatabase
import com.example.isthisahangout.models.movies.Movie
import com.example.isthisahangout.models.movies.toMovie
import com.example.isthisahangout.remotemediator.MoviesRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepository @Inject constructor(
    private val moviesAPI: MoviesAPI,
    private val moviesDatabase: MoviesDatabase
) {
    private val moviesDao = moviesDatabase.getMoviesDao()

    fun getMoviesPaged(): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            remoteMediator = MoviesRemoteMediator(moviesAPI, moviesDatabase),
            pagingSourceFactory = { moviesDao.getMoviesPaged() }
        ).flow.map { pagingData ->
            pagingData.map { movieEntity ->
                movieEntity.toMovie()
            }
        }
}