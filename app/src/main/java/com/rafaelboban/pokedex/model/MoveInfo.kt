package com.rafaelboban.pokedex.model

data class MoveInfo(
    val accuracy: Int,
    val damage_class: DamageClass,
    val generation: Generation,
    val id: Int,
    val name: String,
    val names: List<Name>,
    val power: Int,
    val pp: Int
)