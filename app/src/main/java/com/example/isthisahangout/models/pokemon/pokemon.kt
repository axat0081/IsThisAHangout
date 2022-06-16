package com.example.isthisahangout.models.pokemon

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


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

@Parcelize
@Entity(tableName = "pokemon")
data class Pokemon(
    @PrimaryKey
    val name: String,
    val url: String,
    val image: String
) : Parcelable

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