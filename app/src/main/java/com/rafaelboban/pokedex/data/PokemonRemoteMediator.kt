package com.rafaelboban.pokedex.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.Pokemon

@ExperimentalPagingApi
class PokemonRemoteMediator(
    private val apiService: ApiService,
    private val pokemonDao: PokemonDao,
    private var start: Int = 0,
    private var end: Int = 900
) : RemoteMediator<Int, Pokemon>() {



    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Pokemon>
    ): MediatorResult {
        TODO("Not yet implemented")
    }
}