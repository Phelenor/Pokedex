package com.rafaelboban.pokedex.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.database.PokemonDatabase
import com.rafaelboban.pokedex.database.RemoteKeysDao
import com.rafaelboban.pokedex.utils.Constants.POKEAPI_BASE_URL
import com.rafaelboban.pokedex.utils.Constants.PREFERENCES_DEFAULT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val logging =
            HttpLoggingInterceptor().also { it.setLevel(HttpLoggingInterceptor.Level.BASIC) }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(POKEAPI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

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

    @Singleton
    @Provides
    fun provideSharedPreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(PREFERENCES_DEFAULT, Context.MODE_PRIVATE)
}