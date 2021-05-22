package com.rafaelboban.pokedex.api.di

import com.rafaelboban.pokedex.api.ApiService
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
object RetrofitBuilder {

    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {
        val logging = HttpLoggingInterceptor().also { it.setLevel(HttpLoggingInterceptor.Level.BASIC) }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) =
        retrofit.create(ApiService::class.java)
}