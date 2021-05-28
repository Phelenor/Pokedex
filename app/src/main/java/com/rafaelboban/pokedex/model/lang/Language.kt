package com.rafaelboban.pokedex.model.lang

data class Language(
    val id: Int,
    val name: String,
    val names: List<Name>,
)