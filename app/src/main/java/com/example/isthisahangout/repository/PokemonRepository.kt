package com.example.isthisahangout.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.isthisahangout.api.PokemonAPI
import com.example.isthisahangout.cache.pokemon.PokemonDao
import com.example.isthisahangout.cache.pokemon.PokemonDatabase
import com.example.isthisahangout.cache.pokemon.PokemonKeyDao
import com.example.isthisahangout.models.pokemon.Pokemon
import com.example.isthisahangout.remotemediator.PokemonRemoteMediator
import kotlinx.coroutines.flow.Flow
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
}