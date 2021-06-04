package com.rafaelboban.pokedex.model

import androidx.room.Entity
import java.io.Serializable

@Entity
data class DamageRelations(
    val double_damage_from: List<Type>,
    val double_damage_to: List<Type>,
    val half_damage_from: List<Type>,
    val half_damage_to: List<Type>,
    val no_damage_from: List<Type>,
    val no_damage_to: List<Type>
) : Serializable