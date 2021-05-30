package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.rafaelboban.pokedex.model.lang.LanguageId

@Entity
data class Name(
    @Embedded val language: LanguageId,
    @ColumnInfo(name = "name-name") val name: String
)