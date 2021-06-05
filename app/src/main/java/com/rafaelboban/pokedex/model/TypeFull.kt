package com.rafaelboban.pokedex.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "types")
data class TypeFull(
    @Embedded val damage_relations: DamageRelations,
    @PrimaryKey
    @ColumnInfo(name = "type-id") val id: Int,
    val moves: List<Move>,
    @ColumnInfo(name = "type-name") val name: String,
    val names: List<Name>,
    // @Embedded val move_damage_class: MoveDamageClass,
    val pokemon: List<TypePokemon>
) : Serializable