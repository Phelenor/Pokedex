package com.rafaelboban.pokedex.model

data class PokemonPagedResponse(
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<PokemonId>
)