package com.rafaelboban.pokedex.database

import androidx.room.*
import com.rafaelboban.pokedex.model.PokemonId

@Dao
interface PokemonDao {

    @Query("SELECT * FROM favorites")
    suspend fun getFavorites(): List<PokemonId>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemon: PokemonId)

    @Query("DELETE FROM favorites WHERE name = :name")
    suspend fun delete(name: String)

    @Query("DELETE FROM favorites")
    suspend fun clear()

}