package com.rafaelboban.pokedex.model

import androidx.room.Entity
import java.io.Serializable

@Entity
data class Move(
    val name: String,
    val url: String
) : Serializable