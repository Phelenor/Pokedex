package com.rafaelboban.pokedex.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.database.PokemonDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomBuilder {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context : Context) =
        Room.databaseBuilder(context, PokemonDatabase::class.java, "favorites")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSearchDAO(db: PokemonDatabase): PokemonDao {
        return db.pokemonDao()
    }
}