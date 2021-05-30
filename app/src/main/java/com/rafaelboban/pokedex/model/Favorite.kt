package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "favorite-id") var id: Int? = null,
    @Embedded val pokemon: Pokemon
) {
}