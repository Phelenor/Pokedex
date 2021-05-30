package com.rafaelboban.pokedex.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rafaelboban.pokedex.model.lang.LanguageId

@Entity
data class FlavorTextEntry(
    val flavor_text: String,
    @Embedded val language: LanguageId,
)