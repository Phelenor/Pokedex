package com.rafaelboban.pokedex.model.lang

data class LanguageResponse(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<LanguageId>
)