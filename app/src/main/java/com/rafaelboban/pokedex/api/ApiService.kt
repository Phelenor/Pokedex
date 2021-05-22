package com.rafaelboban.pokedex.api

import com.rafaelboban.pokedex.model.PokemonInfo
import com.rafaelboban.pokedex.model.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("pokemon")
    suspend fun getPokemon(@Query("limit") limit: Int?,
                           @Query("offset") offset: Int?): PokemonResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonInfo(@Path("id") id: Int): PokemonInfo
}