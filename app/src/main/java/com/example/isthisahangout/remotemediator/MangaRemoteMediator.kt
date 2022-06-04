package com.example.isthisahangout.remotemediator

import android.util.Log
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.isthisahangout.api.AnimeAPI
import com.example.isthisahangout.models.MangaRemoteKey
import com.example.isthisahangout.models.MangaResults
import com.example.isthisahangout.cache.manga.MangaDatabase
import retrofit2.HttpException
import java.io.IOException

class MangaRemoteMediator(
    private val api: AnimeAPI,
    private val db: MangaDatabase
) : RemoteMediator<Int, MangaResults.Manga>() {
    private val keyDao = db.getMangaRemoteKeyDao()
    private val mangaDao = db.getMangaDao()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MangaResults.Manga>
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
            val response = api.getManga(page.toString())
            val mangaList = response.top
            val isEndOfList = mangaList.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    keyDao.deleteMangaRemoteKey()
                    mangaDao.deleteManga()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keysList = mangaList.map {
                    MangaRemoteKey(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                keyDao.insertMangaRemoteKey(keysList)
                mangaDao.insertManga(mangaList)
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

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, MangaResults.Manga>
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

    private suspend fun getLastRemoteKey(state: PagingState<Int, MangaResults.Manga>): MangaRemoteKey? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let {
                keyDao.getMangaRemoteKey(it.id)
            }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, MangaResults.Manga>): MangaRemoteKey? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let {
                keyDao.getMangaRemoteKey(it.id)
            }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, MangaResults.Manga>): MangaRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                keyDao.getMangaRemoteKey(id)
            }
        }
    }
}