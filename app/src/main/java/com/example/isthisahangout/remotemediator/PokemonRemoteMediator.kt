package com.example.isthisahangout.remotemediator

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.isthisahangout.api.PokemonAPI
import com.example.isthisahangout.cache.pokemon.PokemonDao
import com.example.isthisahangout.cache.pokemon.PokemonDatabase
import com.example.isthisahangout.cache.pokemon.PokemonKeyDao
import com.example.isthisahangout.models.pokemon.Pokemon
import com.example.isthisahangout.models.pokemon.PokemonRemoteKey
import retrofit2.HttpException
import java.io.IOException
import java.lang.Integer.max

const val PAGE_SIZE = 20

class PokemonRemoteMediator(
    private val pokemonAPI: PokemonAPI,
    private val pokemonDatabase: PokemonDatabase,
    private val pokemonDao: PokemonDao,
    private val pokemonKeyDao: PokemonKeyDao
) : RemoteMediator<Int, Pokemon>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Pokemon>
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
            val response = pokemonAPI.getPokemonPaginated(
                limit = PAGE_SIZE,
                offset = max(0, (page - 1) * PAGE_SIZE)
            ).results.map { pokemonDto ->
                pokemonDto.toPokemon()
            }
            val isEndOfList = response.isEmpty()
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (isEndOfList) null else page + 1
            val pokemonRemoteKeys = response.map { pokemon ->
                PokemonRemoteKey(
                    id = pokemon.name,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
            pokemonDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pokemonDao.deleteAllPokemon()
                    pokemonKeyDao.deletePokemonRemoteKey()
                }
                pokemonDao.insertAllPokemon(response)
                pokemonKeyDao.insertAllPokemonKeys(pokemonRemoteKeys)
            }
            MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: HttpException) {
            MediatorResult.Error(throwable = exception)
        } catch (exception: IOException) {
            MediatorResult.Error(throwable = exception)
        }
    }

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, Pokemon>
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

    private suspend fun getLastRemoteKey(state: PagingState<Int, Pokemon>): PokemonRemoteKey? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let {
                pokemonKeyDao.getPokemonRemoteKeys(it.name)
            }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, Pokemon>): PokemonRemoteKey? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let {
                pokemonKeyDao.getPokemonRemoteKeys(it.name)
            }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, Pokemon>): PokemonRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.name?.let { id ->
                pokemonKeyDao.getPokemonRemoteKeys(id)
            }
        }
    }

}