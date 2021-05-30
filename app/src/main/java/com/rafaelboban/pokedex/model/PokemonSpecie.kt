package com.rafaelboban.pokedex.model

import androidx.room.Embedded
import androidx.room.Entity

@Entity
data class PokemonSpecie(
    @Embedded val evolution_chain: EvolutionChain,
    val flavor_text_entries: List<FlavorTextEntry>,
    val genera: List<Genera>,
    @Embedded val generation: Generation,
    val names: List<Name>,
)