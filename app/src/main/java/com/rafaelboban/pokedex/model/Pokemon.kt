package com.rafaelboban.pokedex.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class Pokemon(
    @PrimaryKey
    @Embedded val idClass: PokemonId,
    @Embedded val specieClass: PokemonSpecie,
    @Embedded val infoClass: PokemonInfo
) {
}