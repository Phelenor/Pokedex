package com.rafaelboban.pokedex.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rafaelboban.pokedex.model.Favorite
import com.rafaelboban.pokedex.model.lang.LanguageId

@Dao
interface PokemonDao {

    @Query("SELECT * FROM favorites")
    suspend fun getFavorites(): List<Favorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemon: Favorite)

    @Query("DELETE FROM favorites WHERE `pokemon-name` = :name")
    suspend fun delete(name: String)

    @Query("DELETE FROM favorites")
    suspend fun clear()

    @Query("SELECT * FROM languages")
    suspend fun getLanguages(): MutableList<LanguageId>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguages(languages: MutableList<LanguageId>)

}