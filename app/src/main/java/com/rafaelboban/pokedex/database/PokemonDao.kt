package com.rafaelboban.pokedex.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rafaelboban.pokedex.model.Favorite
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.lang.LanguageId

@Dao
interface PokemonDao {

    @Query("SELECT * FROM favorites")
    suspend fun getFavorites(): List<Favorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(pokemon: Favorite)

    @Query("DELETE FROM favorites WHERE `pokemon-name` = :name")
    suspend fun deleteFavorite(name: String)

    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: List<Pokemon>)

    @Query("SELECT * FROM pokemon")
    suspend fun getPokemon(): List<Pokemon>


    @Query("SELECT * FROM languages")
    suspend fun getLanguages(): List<LanguageId>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguages(languages: List<LanguageId>)

}