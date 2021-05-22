package com.rafaelboban.pokedex.model

data class PokemonInfo(
    val abilities: List<AbilityInfo>,
    val base_experience: Int,
    val forms: List<Form>,
    val height: Int,
    val held_items: List<Any>,
    val id: Int,
    val is_default: Boolean,
    val location_area_encounters: String,
    val name: String,
    val order: Int,
    val past_types: List<Any>,
    val species: Species,
    val stats: List<StatInfo>,
    val types: List<TypeInfo>,
    val weight: Int
)