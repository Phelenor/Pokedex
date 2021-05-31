package com.rafaelboban.pokedex.data

import android.content.SharedPreferences
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.PokemonInfo
import com.rafaelboban.pokedex.model.PokemonPagedResponse
import com.rafaelboban.pokedex.model.PokemonSpecie
import com.rafaelboban.pokedex.ui.viewmodels.NETWORK_PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.io.IOException

private const val POKEMON_STARTING_PAGE_INDEX = 0

@ExperimentalPagingApi
class PokemonRemoteMediator(
    private val apiService: ApiService,
    private val pokemonDao: PokemonDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val sharedPreferences: SharedPreferences
) : RemoteMediator<Int, Pokemon>() {

    override suspend fun initialize(): InitializeAction {

        return if (!sharedPreferences.contains("initialized")) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Pokemon>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> POKEMON_STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {

            val pokemonPaged = if (page * state.config.pageSize > 898) {
                listOf()
            } else {
                val responsePaged: PokemonPagedResponse =
                    apiService.getPokemon(state.config.pageSize, page * state.config.pageSize)
                responsePaged.results
            }

            if (pokemonPaged.isNotEmpty()) {
                with(sharedPreferences.edit()) {
                    if (!sharedPreferences.contains("initialized")) {
                        putBoolean("initialized", true)
                        apply()
                    }
                }
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
                        break
                    }
                }
                pokemon.add(
                    Pokemon(
                        pokemonPaged[i].pokemonId,
                        pokemonPaged[i],
                        specieInfo[i],
                        pokemonInfo[i]
                    )
                )
            }

            val prevKey = if (page == POKEMON_STARTING_PAGE_INDEX) null else page - 1
            val nextKey = if (pokemon.isEmpty()) {
                null
            } else {
                page + (state.config.pageSize / NETWORK_PAGE_SIZE)
            }
            val keys = pokemon.map {
                RemoteKeys(it.id, prevKey = prevKey, nextKey = nextKey)
            }

            remoteKeysDao.insertAll(keys)
            pokemonDao.insertPokemon(pokemon)

            return MediatorResult.Success(pokemon.isEmpty())
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Pokemon>): RemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { pokemon ->
                remoteKeysDao.remoteKeysPokemonId(pokemon.id)
            }
    }
}