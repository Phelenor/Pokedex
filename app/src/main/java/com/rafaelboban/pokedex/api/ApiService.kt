package com.rafaelboban.pokedex.api

import com.rafaelboban.pokedex.model.*
import com.rafaelboban.pokedex.model.lang.Language
import com.rafaelboban.pokedex.model.lang.LanguageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("pokemon-species")
    suspend fun getPokemon(@Query("limit") limit: Int?,
                           @Query("offset") offset: Int?): PokemonPagedResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonInfo(@Path("id") id: Int): PokemonInfo

    @GET("type/{id}")
    suspend fun getTypeFull(@Path("id") id: Int): TypeFull

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecieInfo(@Path("id") id: Int): PokemonSpecie

    @GET("language/{id}")
    suspend fun getLanguageInfo(@Path("id") id: Int): Language

    @GET("evolution-chain/{id}")
    suspend fun getEvolutionChain(@Path("id") id: Int): EvolutionChainInfo

    @GET("language")
    suspend fun getLanguages(): LanguageResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): PokemonInfo

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecieByName(@Path("name") name: String): PokemonSpecie
}