package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity
data class Generation(
    @ColumnInfo(name = "generation-name") val name: String,
    @ColumnInfo(name = "generation-url") val url: String
) : Serializable