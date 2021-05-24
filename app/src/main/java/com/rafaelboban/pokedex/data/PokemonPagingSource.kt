package com.rafaelboban.pokedex.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.PokemonId
import retrofit2.HttpException
import java.io.IOException

private const val POKEMON_STARTING_PAGE_INDEX = 0

class PokemonPagingSource(
    private val apiService: ApiService,
    private val pokemonDao: PokemonDao
) : PagingSource<Int, PokemonId>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonId> {
        val position = params.key ?: POKEMON_STARTING_PAGE_INDEX

        return try {
            val responsePaged = apiService.getPokemon(params.loadSize, position * params.loadSize)
            val favorites = pokemonDao.getFavorites()
            val pokemonPaged = responsePaged.results

            for (pokemon in pokemonPaged) {
                for (favorite in favorites) {
                    if (pokemon.pokemonId == favorite.pokemonId) {
                        pokemon.isFavorite = true
                        pokemon.id = favorite.id
                        break
                    }
                }
            }

            val nextKey = if (pokemonPaged.isEmpty()) {
                null
            } else {
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }

            for (i in pokemonPaged.indices) {
                if (pokemonPaged[i] in favorites) pokemonPaged[i].isFavorite = true
            }

            LoadResult.Page(
                data = pokemonPaged,
                prevKey = if (position == POKEMON_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )

        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PokemonId>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}