package com.rafaelboban.pokedex.model

import androidx.room.Embedded
import androidx.room.Entity
import java.io.Serializable

@Entity
data class TypePokemon(
    @Embedded val pokemon: PokemonId,
    val slot: Int
) : Serializable {

}