package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity
data class PokemonId(
    @ColumnInfo(name = "pokemon-name") val name: String,
    @ColumnInfo(name = "pokemon-url") val url: String,
    var isFavorite: Boolean = false,
) : Serializable {

//    val pokemonId: Int
//        get() = url.extractPokemonId()
}