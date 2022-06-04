package com.example.isthisahangout.remotemediator

import android.util.Log
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.isthisahangout.api.AnimeAPI
import com.example.isthisahangout.models.AnimeByGenresRemoteKey
import com.example.isthisahangout.models.RoomAnimeByGenres
import com.example.isthisahangout.cache.anime.AnimeDatabase
import retrofit2.HttpException
import java.io.IOException

class AnimeByGenreRemoteMediator(
    private val query: String,
    private val api: AnimeAPI,
    private val db: AnimeDatabase
) : RemoteMediator<Int, RoomAnimeByGenres>() {
    private val keyDao = db.getAnimeByGenreKeyDao()
    private val animeDao = db.getAnimeByGenreDao()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RoomAnimeByGenres>
    ): MediatorResult {
        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }
        return try {
            Log.e("Query", query)
            val response = api.getAnimeByGenre(page = page.toString(), genre = query)
            val resultList = response.results
            val isEndOfList = resultList.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    keyDao.deleteRemoteKeys(query)
                    animeDao.deleteAnimeByGenre(query)
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val animeList = resultList.map {
                    RoomAnimeByGenres(
                        id = it.id,
                        imageUrl = it.imageUrl,
                        url = it.url,
                        synopsis = it.synopsis,
                        genre = query,
                        title = it.title
                    )
                }
                val keysList = animeList.map {
                    AnimeByGenresRemoteKey(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey,
                        genre = query
                    )
                }
                keyDao.insertAll(keysList)
                animeDao.insertAll(animeList)
                MediatorResult.Success(endOfPaginationReached = isEndOfList)
            }
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, RoomAnimeByGenres>
    ): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                    ?: return 1 /*throw InvalidObjectException("Remote key should not be null for $loadType")*/
                remoteKeys.nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                    ?: return 1 /*throw InvalidObjectException("Invalid state, key should not be null")*/
                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                remoteKeys.prevKey
            }
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, RoomAnimeByGenres>): AnimeByGenresRemoteKey? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let {
                keyDao.getRemoteKeys(it.id, query)
            }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, RoomAnimeByGenres>): AnimeByGenresRemoteKey? {
        return state.pages
            .firstOrNull {
                it.data.isNotEmpty()
            }
            ?.data?.firstOrNull()
            ?.let {
                keyDao.getRemoteKeys(it.id, query)
            }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, RoomAnimeByGenres>): AnimeByGenresRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let {
                keyDao.getRemoteKeys(it.id, query)
            }
        }
    }
}