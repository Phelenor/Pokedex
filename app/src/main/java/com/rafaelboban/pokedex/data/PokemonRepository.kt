package com.rafaelboban.pokedex.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.rafaelboban.pokedex.api.ApiService

const val NETWORK_PAGE_SIZE = 20

class PokemonRepository(private val apiService: ApiService) {

    fun getPokemon() =
        Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                maxSize = 100,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { PokemonPagingSource(apiService) }
        ).liveData
}