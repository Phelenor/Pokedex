package com.rafaelboban.pokedex.utils

import androidx.paging.PagingData
import androidx.paging.filter
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.model.MoveInfo
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.Type
import com.rafaelboban.pokedex.model.TypeFull

fun String.extractPokemonSpeciesId(): Int =
    this.substringAfter("pokemon-species").replace("/", "").toInt()

fun String.extractPokemonId(): Int =
    this.substringAfter("pokemon").replace("/", "").toInt()

fun String.extractLangId(): Int =
    this.substringAfter("language").replace("/", "").toInt()

fun String.extractGeneration(): Int =
    this.substringAfter("generation").replace("/", "").toInt()

fun String.extractEvolutionId(): Int =
    this.substringAfter("evolution-chain").replace("/", "").toInt()

fun String.extractMoveId(): Int =
    this.substringAfter("move").replace("/", "").toInt()

fun MoveInfo.getGenerationTint(): Int =
    when (this.generation.name.substringAfter("generation-").uppercase()) {
        "I" -> R.color.flat_pokemon_type_grass
        "II" -> R.color.flat_pokemon_type_bug
        "III" -> R.color.flat_pokemon_type_undefined
        "IV" -> R.color.flat_pokemon_type_ghost
        "V" -> R.color.flat_pokemon_type_water
        "VI" -> R.color.flat_pokemon_type_fighting
        "VII" -> R.color.flat_pokemon_type_fire
        "VIII" -> R.color.flat_pokemon_type_poison
        else -> R.color.flat_pokemon_type_fairy
    }


fun MoveInfo.getCategoryTint(): Int =
    when (this.damage_class.name.capitalize()) {
        "Physical" -> R.color.error
        "Status" -> R.color.dark_alpha
        "Special" -> R.color.cold_gray
        else -> R.color.success
    }

fun MoveInfo.getMaxPP() =
    when (this.pp) {
        5 -> 8
        10 -> 16
        15 -> 24
        20 -> 32
        25 -> 40
        30 -> 48
        35 -> 56
        40 -> 64
        else -> 1
    }

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
        "electric" -> R.color.flat_pokemon_type_undefined
        else -> R.color.black
}


fun TypeFull.getColor() =
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
