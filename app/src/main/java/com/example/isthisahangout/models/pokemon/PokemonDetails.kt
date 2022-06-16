package com.example.isthisahangout.models.pokemon

data class PokemonDetails(
    val name: String,
    val weight: Int,
    val moves: List<Moves>
)

data class Moves(
    val move: MovesDetails
)

data class MovesDetails(
    val name: String
)