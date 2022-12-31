package com.example.isthisahangout.remotemediator

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.isthisahangout.api.AnimeAPI
import com.example.isthisahangout.cache.anime.AnimeDatabase
import com.example.isthisahangout.models.AnimeBySeasonsRemoteKey
import com.example.isthisahangout.models.RoomAnimeBySeasons
import retrofit2.HttpException
import java.io.IOException

class AnimeBySeasonsRemoteMediator(
    private val year: String,
    private val season: String,
    private val db: AnimeDatabase,
    private val api: AnimeAPI
) : RemoteMediator<Int, RoomAnimeBySeasons>() {

    private val animeDao = db.getAnimeBySeasonDao()
    private val keyDao = db.getAnimeBySeasonsRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RoomAnimeBySeasons>
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
            val response = api.getAnimeBySeason(season = season, year = year)
            val resultList = response.data
            val isEndOfList = resultList.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    keyDao.deleteRemoteKeys(season = season, year = year)
                    animeDao.deleteAll(season = season, year = year)
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val animeList = resultList.map {
                    RoomAnimeBySeasons(
                        id = it.id,
                        title = it.title,
                        imageUrl = it.images.jpg.image_url,
                        url = it.url,
                        synopsis = it.synopsis,
                        favorites = it.favorites ?: 0,
                        season = season,
                        year = year
                    )
                }
                val keysList = animeList.map {
                    AnimeBySeasonsRemoteKey(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey,
                        season = season,
                        year = year
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
        state: PagingState<Int, RoomAnimeBySeasons>
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

    private suspend fun getLastRemoteKey(state: PagingState<Int, RoomAnimeBySeasons>): AnimeBySeasonsRemoteKey? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let {
                keyDao.getRemoteKeys(it.id, season = season, year = year)
            }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, RoomAnimeBySeasons>): AnimeBySeasonsRemoteKey? {
        return state.pages
            .firstOrNull {
                it.data.isNotEmpty()
            }
            ?.data?.firstOrNull()
            ?.let {
                keyDao.getRemoteKeys(it.id, season = season, year = year)
            }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, RoomAnimeBySeasons>): AnimeBySeasonsRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let {
                keyDao.getRemoteKeys(it.id, season = season, year = year)
            }
        }
    }

}