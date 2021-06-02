package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity
data class Type(
    @ColumnInfo(name = "type-name") val name: String,
    @ColumnInfo(name = "type-url") val url: String
) : Serializable