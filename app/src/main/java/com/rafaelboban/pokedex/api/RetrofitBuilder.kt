package com.rafaelboban.pokedex.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    private fun getRetrofit() : Retrofit {

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
    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}