package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity
data class EvolutionChain(
    @ColumnInfo(name = "evolution-chain-url") val url: String
) : Serializable