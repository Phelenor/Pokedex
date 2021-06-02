package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "pokemon")
data class Pokemon(
    @PrimaryKey
    @ColumnInfo(name = "master-id") val id: Int,
    @Embedded val idClass: PokemonId,
    @Embedded val specieClass: PokemonSpecie,
    @Embedded val infoClass: PokemonInfo
) : Serializable {
}