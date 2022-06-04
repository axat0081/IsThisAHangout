package com.example.isthisahangout.remotemediator

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.isthisahangout.api.AnimeAPI
import com.example.isthisahangout.models.RoomMangaByGenre
import com.example.isthisahangout.models.RoomMangaByGenreRemoteKey
import com.example.isthisahangout.cache.manga.MangaDatabase
import retrofit2.HttpException
import java.io.IOException

class MangaByGenreRemoteMediator(
    private val genre: String,
    private val animeAPI: AnimeAPI,
    private val mangaDb: MangaDatabase
) : RemoteMediator<Int, RoomMangaByGenre>() {
    private val mangaDao = mangaDb.getMangaDao()
    private val mangaRemoteKeyDao = mangaDb.getMangaRemoteKeyDao()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RoomMangaByGenre>
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
            val response = animeAPI.getMangaByGenre(page = page, genre = genre)
            val resultList = response.results
            val isEndOfList = resultList.isEmpty()
            mangaDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    mangaRemoteKeyDao.deleteMangaByGenreRemoteKey(genre)
                    mangaDao.deleteMangaByGenre(genre)
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val mangaList = resultList.map {
                    RoomMangaByGenre(
                        id = it.id,
                        imageUrl = it.imageUrl,
                        url = it.url,
                        synopsis = it.synopsis,
                        genre = genre,
                        title = it.title
                    )
                }
                val keysList = mangaList.map {
                    RoomMangaByGenreRemoteKey(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey,
                        genre = genre
                    )
                }
                mangaRemoteKeyDao.insertMangaByGenreRemoteKey(keysList)
                mangaDao.insertRoomMangaByGenre(mangaList)
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
        state: PagingState<Int, RoomMangaByGenre>
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

    private suspend fun getLastRemoteKey(state: PagingState<Int, RoomMangaByGenre>): RoomMangaByGenreRemoteKey? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let {
                mangaRemoteKeyDao.getMangaByGenreRemoteKey(it.id, genre)
            }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, RoomMangaByGenre>): RoomMangaByGenreRemoteKey? {
        return state.pages
            .firstOrNull {
                it.data.isNotEmpty()
            }
            ?.data?.firstOrNull()
            ?.let {
                mangaRemoteKeyDao.getMangaByGenreRemoteKey(it.id, genre)
            }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, RoomMangaByGenre>): RoomMangaByGenreRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let {
                mangaRemoteKeyDao.getMangaByGenreRemoteKey(it.id, genre)
            }
        }
    }
}