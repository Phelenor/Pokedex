package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class Ability(
    @ColumnInfo(name = "ability-name") val name: String,
    @ColumnInfo(name = "ability-url") val url: String
)