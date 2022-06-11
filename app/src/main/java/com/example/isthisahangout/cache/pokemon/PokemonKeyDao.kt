package com.example.isthisahangout.cache.pokemon

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.pokemon.PokemonRemoteKey

@Dao
interface PokemonKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPokemonKeys(list: List<PokemonRemoteKey>)

    @Query("SELECT * FROM pokemon_remote_key where id = :id")
    suspend fun getPokemonRemoteKeys(id: String): PokemonRemoteKey

    @Query("DELETE FROM pokemon_remote_key")
    suspend fun deletePokemonRemoteKey()
}