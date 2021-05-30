package com.rafaelboban.pokedex.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.*
import com.rafaelboban.pokedex.ui.viewmodels.NETWORK_PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.io.IOException

private const val POKEMON_STARTING_PAGE_INDEX = 0

class PokemonPagingSource(
    private val apiService: ApiService,
    private val pokemonDao: PokemonDao,
    private var start: Int = 0,
    private var end: Int = 900
) : PagingSource<Int, Pokemon>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        val position = params.key ?: POKEMON_STARTING_PAGE_INDEX

        return try {

            lateinit var responsePaged: PokemonPagedResponse
            lateinit var pokemonPaged: List<PokemonId>

            if (end - start < 60) end += 60 - (end - start)

            if (position * params.loadSize + start + params.loadSize <= end) {
                responsePaged = apiService.getPokemon(params.loadSize, position * params.loadSize + start)
                pokemonPaged = responsePaged.results
            } else {
                pokemonPaged = listOf()
            }

            val favorites = pokemonDao.getFavorites()

            val specieInfo: List<PokemonSpecie>
            val pokemonInfo: List<PokemonInfo>
            coroutineScope {

                val fetchSpecieInfo = pokemonPaged.map { pokemonId ->
                    async(Dispatchers.IO) {
                        apiService.getPokemonSpecieInfo(pokemonId.pokemonId)
                    }
                }

                val fetchPokemonInfo = pokemonPaged.map { pokemonId ->
                    async(Dispatchers.IO) {
                        apiService.getPokemonInfo(pokemonId.pokemonId)
                    }
                }

                specieInfo = fetchSpecieInfo.awaitAll()
                pokemonInfo = fetchPokemonInfo.awaitAll()
            }

            val pokemon = mutableListOf<Pokemon>()

            for (i in pokemonPaged.indices) {
                for (favorite in favorites) {
                    if (pokemonPaged[i].pokemonId == favorite.pokemon.idClass.pokemonId) {
                        pokemonPaged[i].isFavorite = true
                        pokemonPaged[i].id = favorite.id
                        break
                    }
                }
                pokemon.add(Pokemon(pokemonPaged[i], specieInfo[i], pokemonInfo[i]))
            }

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