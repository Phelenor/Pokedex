package com.rafaelboban.pokedex.model

import androidx.room.Embedded
import androidx.room.Entity

@Entity
data class Pokemon(
    @Embedded val idClass: PokemonId,
    @Embedded val specieClass: PokemonSpecie,
    @Embedded val infoClass: PokemonInfo
) {
}