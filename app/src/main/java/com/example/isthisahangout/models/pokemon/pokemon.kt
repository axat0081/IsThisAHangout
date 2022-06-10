package com.example.isthisahangout.models.pokemon

import androidx.room.Entity
import androidx.room.PrimaryKey


data class PokemonResponse(
    val results: List<PokemonDto>
)

data class PokemonDto(
    val name: String,
    val url: String
) {
    fun toPokemon(): Pokemon =
        Pokemon(
            name = name,
            url = url,
            image = getPokemonImageUrl(url)
        )
}

@Entity(tableName = "pokemon")
data class Pokemon(
    @PrimaryKey
    val name: String,
    val url: String,
    val image: String
)

fun getPokemonImageUrl(url: String): String {
    val number = if (url.endsWith("/")) {
        url.dropLast(1).takeLastWhile { it.isDigit() }
    } else {
        url.takeLastWhile { it.isDigit() }
    }
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
}

@Entity(tableName = "pokemon_remote_key")
data class PokemonRemoteKey(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
)