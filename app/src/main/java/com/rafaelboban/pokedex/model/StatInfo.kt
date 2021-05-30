package com.rafaelboban.pokedex.model

import androidx.room.Embedded
import androidx.room.Entity

@Entity
data class StatInfo(
    val base_stat: Int,
    val effort: Int,
    @Embedded val stat: Stat
)