package com.rafaelboban.pokedex.model

import androidx.room.Entity
import java.io.Serializable

@Entity
data class PokemonInfo(
    val abilities: List<AbilityInfo>,
    val height: Int,
    val order: Int,
    val stats: List<StatInfo>,
    val types: List<TypeInfo>,
    val weight: Int
) : Serializable