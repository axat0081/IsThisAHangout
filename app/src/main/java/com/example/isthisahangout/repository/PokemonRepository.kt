package com.example.isthisahangout.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.isthisahangout.api.PokemonAPI
import com.example.isthisahangout.cache.pokemon.PokemonDao
import com.example.isthisahangout.cache.pokemon.PokemonDatabase
import com.example.isthisahangout.cache.pokemon.PokemonKeyDao
import com.example.isthisahangout.models.pokemon.Pokemon
import com.example.isthisahangout.models.pokemon.PokemonDetails
import com.example.isthisahangout.remotemediator.PokemonRemoteMediator
import com.example.isthisahangout.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val pokemonAPI: PokemonAPI,
    private val pokemonDatabase: PokemonDatabase,
    private val pokemonDao: PokemonDao,
    private val pokemonKeyDao: PokemonKeyDao
) {
    fun getPokemonPaginated(): Flow<PagingData<Pokemon>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 40
            ),
            remoteMediator = PokemonRemoteMediator(
                pokemonAPI = pokemonAPI,
                pokemonDatabase = pokemonDatabase,
                pokemonDao = pokemonDao,
                pokemonKeyDao = pokemonKeyDao
            ),
            pagingSourceFactory = { pokemonDao.getPokemonPaginated() }
        ).flow

    fun getPokemonDetails(name: String): Flow<Resource<PokemonDetails>> = flow {
        emit(Resource.Loading())
        try {
            val pokemonDetails = pokemonAPI.getPokemonDetails(name)
            emit(Resource.Success(pokemonDetails))
        } catch (exception: HttpException) {
            emit(Resource.Error(throwable = exception))
        } catch (exception: IOException) {
            emit(Resource.Error(throwable = exception))
        }
    }
}