package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity
data class AbilityInfo(
    @Embedded val ability: Ability,
    val is_hidden: Boolean,
    @ColumnInfo(name = "type-info-slot") val slot: Int
)