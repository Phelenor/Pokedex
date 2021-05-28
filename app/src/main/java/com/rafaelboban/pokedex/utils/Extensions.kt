package com.rafaelboban.pokedex.utils

import androidx.paging.PagingData
import androidx.paging.filter
import com.rafaelboban.pokedex.model.PokemonId

fun String.extractPokemonId(): Int =
    this.substringAfter("pokemon").replace("/", "").toInt()

fun String.extractLangId(): Int =
    this.substringAfter("language").replace("/", "").toInt()


fun String.transformToRange(): IntRange? {
    val range = this.split("-")
    if (range.size == 2) {
        try {
            val start = range[0].toInt()
            val end = range[1].toInt()
            return start..end
        } catch (e: Exception) {
        }
        try {
            val start = range[0].toInt()
            return start..11000
        } catch (e: Exception) {
        }
    }
    return null
}

fun PokemonId.getSprite() =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${this.pokemonId}.png"

fun <T : Any> PagingData<T>.filterAll(filters: List<(T) -> Boolean>) =
    filter { item -> filters.any { filter -> filter(item) } }