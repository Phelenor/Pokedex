package com.rafaelboban.pokedex.model

import androidx.room.Embedded
import androidx.room.Entity
import com.rafaelboban.pokedex.model.lang.LanguageId

@Entity
data class Genera(
    val genus: String,
    @Embedded val language: LanguageId
)