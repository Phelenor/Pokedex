package com.rafaelboban.pokedex.utils

import androidx.paging.PagingData
import androidx.paging.filter
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.PokemonId
import com.rafaelboban.pokedex.model.Type

fun String.extractPokemonId(): Int =
    this.substringAfter("pokemon-species").replace("/", "").toInt()

fun String.extractLangId(): Int =
    this.substringAfter("language").replace("/", "").toInt()

fun String.extractGeneration(): Int =
    this.substringAfter("generation").replace("/", "").toInt()

fun String.extractEvolutionId(): Int =
    this.substringAfter("evolution-chain").replace("/", "").toInt()

fun PokemonId.getSprite() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${this.pokemonId}.png"

fun Pokemon.getSprite() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${this.infoClass.id}.png"

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

fun Type.getColor() =
    when (this.name) {
        "grass" -> R.color.flat_pokemon_type_grass
        "fire" -> R.color.flat_pokemon_type_fire
        "bug" -> R.color.flat_pokemon_type_bug
        "dark" -> R.color.flat_pokemon_type_dark
        "steel" -> R.color.flat_pokemon_type_steel
        "poison" -> R.color.flat_pokemon_type_poison
        "water" -> R.color.flat_pokemon_type_water
        "fighting" -> R.color.flat_pokemon_type_fighting
        "flying" -> R.color.flat_pokemon_type_flying
        "rock" -> R.color.flat_pokemon_type_rock
        "ground" -> R.color.flat_pokemon_type_ground
        "ghost" -> R.color.flat_pokemon_type_ghost
        "psychic" -> R.color.flat_pokemon_type_psychic
        "fairy" -> R.color.flat_pokemon_type_fairy
        "dragon" -> R.color.flat_pokemon_type_dragon
        "ice" -> R.color.flat_pokemon_type_ice
        "normal" -> R.color.flat_pokemon_type_normal
        "electric" -> R.color.flat_pokemon_type_electric
        else -> R.color.black
}
