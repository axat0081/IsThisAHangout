package com.example.isthisahangout.remotemediator

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.isthisahangout.api.MoviesAPI
import com.example.isthisahangout.cache.movies.MoviesDatabase
import com.example.isthisahangout.models.movies.MovieEntity
import com.example.isthisahangout.models.movies.MoviesRemoteKey
import com.example.isthisahangout.models.movies.toMovieEntity
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val MOVIES_STARTING_PAGE_NUMBER = 1

class MoviesRemoteMediator(
    private val moviesAPI: MoviesAPI,
    private val moviesDatabase: MoviesDatabase,
) : RemoteMediator<Int, MovieEntity>() {
    private val moviesDao = moviesDatabase.getMoviesDao()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.REFRESH -> MOVIES_STARTING_PAGE_NUMBER
            LoadType.APPEND -> moviesDao.getMoviesRemoteKey()?.nextPage
                ?: MOVIES_STARTING_PAGE_NUMBER
        }
        try {
            val moviesDto = moviesAPI.getMovies(page = page)
            moviesDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    moviesDao.deleteAllMovies()
                }
                moviesDao.insertAllMovies(
                    moviesDto.results.map { movieDto ->
                        movieDto.toMovieEntity()
                    }
                )
                val nextPage = page + 1
                moviesDao.updateRemoteKey(MoviesRemoteKey(nextPage))
            }
            return MediatorResult.Success(endOfPaginationReached = moviesDto.results.isEmpty())
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    override suspend fun initialize(): InitializeAction {
        val lastUpdatedAt = moviesDao.getLastUpdatedAt()
        return lastUpdatedAt?.let { time ->
            if (System.currentTimeMillis() - time > TimeUnit.MINUTES.toMillis(10))
                InitializeAction.SKIP_INITIAL_REFRESH
            else InitializeAction.LAUNCH_INITIAL_REFRESH
        } ?: InitializeAction.LAUNCH_INITIAL_REFRESH
    }
}