package com.rafaelboban.pokedex.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rafaelboban.pokedex.utils.extractId
import java.io.Serializable

@Entity(tableName = "favorites")
data class PokemonId(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val name: String,
    val url: String,
    var isFavorite: Boolean = false
) : Serializable {

    val pokemonId: Int
        get() = url.extractId()
}