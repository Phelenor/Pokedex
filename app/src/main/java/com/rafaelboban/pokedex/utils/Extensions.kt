package com.rafaelboban.pokedex.utils

import com.rafaelboban.pokedex.model.PokemonId

fun String.extractId(): Int {
    return this.substringAfter("pokemon").replace("/", "").toInt()
}

fun PokemonId.getSprite() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${this.url.extractId()}.png"