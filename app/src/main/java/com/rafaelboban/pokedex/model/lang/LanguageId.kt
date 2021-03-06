package com.rafaelboban.pokedex.model.lang

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "languages")
data class LanguageId(
    @PrimaryKey
    val name: String,
    val url: String,
    var nameEnglish: String? = null,
    var nameNative: String? = null
) : Serializable