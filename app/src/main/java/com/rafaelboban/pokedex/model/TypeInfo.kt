package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity
data class TypeInfo(
    @ColumnInfo(name = "type-info-slot") val slot: Int,
    @Embedded val type: Type
)