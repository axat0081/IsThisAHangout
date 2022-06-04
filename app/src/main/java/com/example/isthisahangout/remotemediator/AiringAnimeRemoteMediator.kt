package com.example.isthisahangout.remotemediator

import android.util.Log
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.isthisahangout.api.AnimeAPI
import com.example.isthisahangout.models.AiringAnimeRemoteKey
import com.example.isthisahangout.models.AiringAnimeResponse
import com.example.isthisahangout.cache.anime.AnimeDatabase
import retrofit2.HttpException
import java.io.IOException

class AiringAnimeRemoteMediator(
    private val api: AnimeAPI,
    private val db: AnimeDatabase
) : RemoteMediator<Int, AiringAnimeResponse.AiringAnime>() {
    private val keyDao = db.getAiringAnimeRemoteKeyDao()
    private val animeDao = db.getAiringAnimeDoa()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AiringAnimeResponse.AiringAnime>
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
            val response = api.getAiringAnime(page.toString())
            val animeList = response.top
            val isEndOfList = animeList.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    keyDao.deleteRemoteKeys()
                    animeDao.deleteAnime()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keysList = animeList.map {
                    AiringAnimeRemoteKey(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                keyDao.insertAll(keysList)
                animeDao.insertAll(animeList)
                MediatorResult.Success(endOfPaginationReached = isEndOfList)
            }
        } catch (exception: IOException) {
            Log.e("Error", exception.message.toString())
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            Log.e("Error", exception.message.toString())
            MediatorResult.Error(exception)
        }
    }

    suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, AiringAnimeResponse.AiringAnime>
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
                val remoteKeys = getFirstRemoteKey(state) ?: return 1
                /*?: throw InvalidObjectException("Invalid state, key should not be null")*/
                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                remoteKeys.prevKey
            }
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, AiringAnimeResponse.AiringAnime>): AiringAnimeRemoteKey? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let {
                keyDao.getRemoteKeys(it.id)
            }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, AiringAnimeResponse.AiringAnime>): AiringAnimeRemoteKey? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let {
                keyDao.getRemoteKeys(it.id)
            }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, AiringAnimeResponse.AiringAnime>): AiringAnimeRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                keyDao.getRemoteKeys(id)
            }
        }
    }
}