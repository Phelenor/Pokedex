package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import java.io.Serializable

@Entity
data class AbilityInfo(
    @Embedded val ability: Ability,
    val is_hidden: Boolean,
    @ColumnInfo(name = "ability-info-slot") val slot: Int
) : Serializable