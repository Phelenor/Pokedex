package com.rafaelboban.pokedex.utils

import androidx.paging.PagingData
import androidx.paging.filter
import com.rafaelboban.pokedex.model.PokemonId

fun String.extractPokemonId(): Int =
    this.substringAfter("pokemon-species").replace("/", "").toInt()

fun String.extractLangId(): Int =
    this.substringAfter("language").replace("/", "").toInt()

fun String.extractGeneration(): Int =
    this.substringAfter("generation").replace("/", "").toInt()

fun PokemonId.getSprite() =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${this.pokemonId}.png"

fun Int.toRoman() =
    when (this) {
        1 -> 'I'
        2 -> "II"
        3 -> "III"
        4 -> "IV"
        5 -> "V"
        6 -> "VI"
        7 -> "VII"
        8 -> "VIII"
        else -> "X"
    }

fun <T : Any> PagingData<T>.filterAll(filters: List<(T) -> Boolean>) =
    filter { item -> filters.any { filter -> filter(item) } }
