package com.example.isthisahangout.remotemediator

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.isthisahangout.api.GameAPI
import com.example.isthisahangout.models.GameRemoteKey
import com.example.isthisahangout.models.RoomGames
import com.example.isthisahangout.cache.games.GameDatabase
import retrofit2.HttpException
import java.io.IOException

class GamesRemoteMediator(
    private val api: GameAPI,
    private val db: GameDatabase
) : RemoteMediator<Int, RoomGames>() {
    private val gameDao = db.getGamesDao()
    private val keyDao = db.getGamesRemoteKeyDao()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RoomGames>
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
            val response =
                api.getGames(page = page.toString(), pageSize = state.config.pageSize.toString())
            val results = response.results
            val isEndOfList = results.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    keyDao.deleteGameRemoteKey()
                    gameDao.deleteGames()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val gameList = results.map {
                    val screenshots = ArrayList<String?>()
                    val genres = ArrayList<String?>()
                    it.picList.map { screenShots ->
                        screenshots.add(screenShots.image)
                    }
                    it.genres.map {g->
                        genres.add(g.name)
                    }
                    RoomGames(
                        id = it.id,
                        name = it.name,
                        rating = it.rating,
                        imageUrl = it.imageUrl,
                        screenshots = screenshots,
                        genres = genres
                    )
                }
                val keyList = gameList.map {
                    GameRemoteKey(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                keyDao.insertAll(keyList)
                gameDao.insertAll(gameList)
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
        state: PagingState<Int, RoomGames>
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

    private suspend fun getLastRemoteKey(state: PagingState<Int, RoomGames>): GameRemoteKey? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let {
                keyDao.getRemoteKeys(it.id)
            }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, RoomGames>): GameRemoteKey? {
        return state.pages
            .firstOrNull() {
                it.data.isNotEmpty()
            }
            ?.data?.firstOrNull()
            ?.let {
                keyDao.getRemoteKeys(it.id)
            }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, RoomGames>): GameRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                keyDao.getRemoteKeys(id)
            }
        }
    }
}