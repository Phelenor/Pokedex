package com.rafaelboban.pokedex.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rafaelboban.pokedex.model.lang.LanguageId
import com.rafaelboban.pokedex.model.PokemonId

@Database(entities = [PokemonId::class, LanguageId::class], version = 1, exportSchema = false)
abstract class PokemonDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
}