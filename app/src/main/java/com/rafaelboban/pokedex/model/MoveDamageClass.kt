package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity
data class MoveDamageClass(
    @ColumnInfo(name = "move-damage-name") val name: String,
    val url: String
) : Serializable