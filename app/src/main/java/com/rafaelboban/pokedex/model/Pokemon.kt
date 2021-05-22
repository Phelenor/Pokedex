package com.rafaelboban.pokedex.model

data class Pokemon(
    var idClass: PokemonId,
    var infoClass: PokemonInfo? = null
) {
}