package com.example.isthisahangout.cache.pokemon

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.isthisahangout.models.pokemon.Pokemon

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon")
    fun getPokemonPaginated(): PagingSource<Int,Pokemon>

    @Insert(onConflict = REPLACE)
    suspend fun insertAllPokemon(pokemonList: List<Pokemon>)

    @Query("DELETE FROM pokemon")
    suspend fun deleteAllPokemon()
}