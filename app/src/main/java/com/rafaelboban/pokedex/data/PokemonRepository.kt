package com.rafaelboban.pokedex.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.database.PokemonDao
import javax.inject.Inject
import javax.inject.Singleton

const val NETWORK_PAGE_SIZE = 20

@Singleton
class PokemonRepository @Inject constructor(
    private val apiService: ApiService,
    private val pokemonDao: PokemonDao
    ) {

    fun getPokemon() =
        Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PokemonPagingSource(apiService, pokemonDao) }
        ).liveData

}