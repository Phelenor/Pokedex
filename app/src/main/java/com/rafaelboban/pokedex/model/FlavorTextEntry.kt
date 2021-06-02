package com.rafaelboban.pokedex.model

import androidx.room.Embedded
import androidx.room.Entity
import com.rafaelboban.pokedex.model.lang.LanguageId
import java.io.Serializable

@Entity
data class FlavorTextEntry(
    val flavor_text: String,
    @Embedded val language: LanguageId,
) : Serializable