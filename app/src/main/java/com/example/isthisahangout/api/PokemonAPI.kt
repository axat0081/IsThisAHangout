package com.example.isthisahangout.api

import com.example.isthisahangout.models.pokemon.PokemonDetails
import com.example.isthisahangout.models.pokemon.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonAPI {
    companion object {
        const val BASE_URl = "https://pokeapi.co/api/v2/"
    }

    @GET("pokemon")
    suspend fun getPokemonPaginated(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetails(
        @Path("name") name: String
    ): PokemonDetails

}