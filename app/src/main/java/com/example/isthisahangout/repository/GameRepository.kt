package com.example.isthisahangout.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.isthisahangout.api.GameAPI
import com.example.isthisahangout.models.RoomGames
import com.example.isthisahangout.remotemediator.GamesRemoteMediator
import com.example.isthisahangout.cache.games.GameDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    val api: GameAPI,
    val db: GameDatabase
) {
    val gameDoa = db.getGamesDao()
    fun getGames(): Flow<PagingData<RoomGames>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 40
            ),
            remoteMediator = GamesRemoteMediator(api,db),
            pagingSourceFactory = { gameDoa.getGames() }
        ).flow
}