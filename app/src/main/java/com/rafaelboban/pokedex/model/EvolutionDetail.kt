package com.rafaelboban.pokedex.model


data class EvolutionDetail(
    val held_item: Item,
    val item: Item,
    val location: Any,
    val min_level: Int,
    val time_of_day: String,
    val trigger: Trigger,
)