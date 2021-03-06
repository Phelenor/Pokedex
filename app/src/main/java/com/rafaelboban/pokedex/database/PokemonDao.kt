package com.rafaelboban.pokedex.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rafaelboban.pokedex.model.Favorite
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.TypeFull
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

    @Query("UPDATE pokemon SET isFavorite = 0")
    suspend fun deleteFavoriteStatus()


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemon(pokemon: List<Pokemon>)

    @Query("SELECT * FROM pokemon")
    fun getPokemon(): PagingSource<Int, Pokemon>

    @Query("SELECT * FROM pokemon WHERE `pokemon-name` = :name")
    suspend fun getSinglePokemon(name: String): Pokemon

    @Query("SELECT * FROM pokemon WHERE `info-id` = :id")
    suspend fun getSinglePokemon(id: Int): Pokemon

    @Query("UPDATE pokemon SET isFavorite = :favorite WHERE `master-id` = :id")
    suspend fun updatePokemon(id: Int, favorite: Int)


    @Query("SELECT * FROM languages")
    suspend fun getLanguages(): List<LanguageId>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguages(languages: List<LanguageId>)


    @Query("SELECT * FROM types")
    suspend fun getTypes(): List<TypeFull>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypes(types: List<TypeFull>)

}