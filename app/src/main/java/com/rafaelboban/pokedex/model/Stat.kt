package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity
data class Stat(
    @ColumnInfo(name = "stat-name") val name: String,
    @ColumnInfo(name = "stat-url") val url: String
) : Serializable