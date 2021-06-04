package com.rafaelboban.pokedex.model


data class Item(
    val flavor_text_entries: List<FlavorTextEntry>,
    val held_by_pokemon: List<PokemonId>,
    val id: Int,
    val name: String,
    val names: List<Name>,
    val sprites: Sprites
)