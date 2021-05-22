package com.rafaelboban.pokedex.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.rafaelboban.pokedex.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

const val NETWORK_PAGE_SIZE = 20

@Singleton
class PokemonRepository @Inject constructor(private val apiService: ApiService) {

    fun getPokemon(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                maxSize = 100,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { PokemonPagingSource(apiService, query) }
        ).liveData
}