package com.rafaelboban.pokedex.utils

import android.util.Range
import androidx.paging.PagingData
import androidx.paging.filter
import com.rafaelboban.pokedex.model.PokemonId
import java.lang.Exception

fun String.extractId(): Int {
    return this.substringAfter("pokemon").replace("/", "").toInt()
}

fun String.transformToRange(): IntRange? {
    val range = this.split("-")
    if (range.size == 2) {
        try {
            val start = range[0].toInt()
            val end = range[1].toInt()
            return start..end
        } catch (e: Exception) {
        }
        try {
            val start = range[0].toInt()
            return start..1118
        } catch (e: Exception) {
        }
    }
    return null
}

fun PokemonId.getSprite() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${this.url.extractId()}.png"

fun <T : Any> PagingData<T>.filterAll(filters: List<(T) -> Boolean>) =
    filter { item -> filters.any { filter -> filter(item) } }