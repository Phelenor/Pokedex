package com.rafaelboban.pokedex.api

import com.rafaelboban.pokedex.model.PokemonInfo
import com.rafaelboban.pokedex.model.PokemonResponse
import com.rafaelboban.pokedex.model.lang.Language
import com.rafaelboban.pokedex.model.lang.LanguageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("pokemon")
    suspend fun getPokemon(@Query("limit") limit: Int?,
                           @Query("offset") offset: Int?): PokemonResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonInfo(@Path("id") id: Int): PokemonInfo

    @GET("language/{id}")
    suspend fun getLanguageInfo(@Path("id") id: Int): Language

    @GET("language")
    suspend fun getLanguages(): LanguageResponse
}