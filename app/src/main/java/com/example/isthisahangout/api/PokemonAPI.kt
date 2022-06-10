package com.example.isthisahangout.api

import com.example.isthisahangout.models.pokemon.PokemonResponse
import retrofit2.http.Query

interface PokemonAPI {
    companion object {
        const val BASE_URl = "https://pokeapi.co/api/v2/"
    }

    suspend fun getPokemonPaginated(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonResponse


}