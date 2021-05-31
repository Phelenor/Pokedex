package com.rafaelboban.pokedex.di

import android.app.Application
import androidx.room.Room
import com.rafaelboban.pokedex.data.RemoteKeysDao
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.database.PokemonDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomBuilder {
    @Singleton
    @Provides
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, PokemonDatabase::class.java, "favorites")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePokemonDao(db: PokemonDatabase): PokemonDao {
        return db.pokemonDao()
    }

    @Provides
    fun provideRemoteKeysDao(db: PokemonDatabase): RemoteKeysDao {
        return db.remoteKeysDao()
    }
}