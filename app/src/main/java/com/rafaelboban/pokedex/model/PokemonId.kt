package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.rafaelboban.pokedex.utils.extractPokemonId

@Entity
data class PokemonId(
    @ColumnInfo(name = "pokemon-name") val name: String,
    @ColumnInfo(name = "pokemon-url") val url: String,
    var isFavorite: Boolean = false,
) {

    val pokemonId: Int
        get() = url.extractPokemonId()
}