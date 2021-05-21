package com.rafaelboban.pokedex.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.model.Pokemon
import retrofit2.HttpException
import java.io.IOException

private const val POKEMON_STARTING_PAGE_INDEX = 0

class PokemonPagingSource(private val apiService: ApiService,
                          private val query: String = "") : PagingSource<Int, Pokemon>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        val position = params.key ?: POKEMON_STARTING_PAGE_INDEX

        return try {
            val response = apiService.getPokemon(params.loadSize, position * params.loadSize)
            val pokemon = response.results
            val nextKey = if (pokemon.isEmpty()) {
                null
            } else {
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }

            LoadResult.Page(
                data = pokemon,
                prevKey = if (position == POKEMON_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )

        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Pokemon>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}