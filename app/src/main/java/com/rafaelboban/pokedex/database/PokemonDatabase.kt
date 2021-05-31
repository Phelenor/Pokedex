package com.rafaelboban.pokedex.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rafaelboban.pokedex.data.RemoteKeys
import com.rafaelboban.pokedex.data.RemoteKeysDao
import com.rafaelboban.pokedex.model.Favorite
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.lang.LanguageId
import com.rafaelboban.pokedex.utils.Converters

@Database(entities = [Pokemon::class, Favorite::class, LanguageId::class, RemoteKeys::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PokemonDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}