package com.example.isthisahangout.cache.pokemon

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.pokemon.Pokemon
import com.example.isthisahangout.models.pokemon.PokemonRemoteKey

@Database(
    entities = [Pokemon::class,
               PokemonRemoteKey::class],
    version = 1
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun getPokemonDao(): PokemonDao
    abstract fun getPokemonKeyDao(): PokemonKeyDao
}