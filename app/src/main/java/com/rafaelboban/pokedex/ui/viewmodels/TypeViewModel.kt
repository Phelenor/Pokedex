package com.rafaelboban.pokedex.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.*
import com.rafaelboban.pokedex.utils.extractPokemonId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class TypeViewModel @Inject constructor(
    private val apiService: ApiService,
    val pokemonDao: PokemonDao
) : ViewModel() {

    val types = MutableLiveData<List<TypeFull>>()
    val moves = MutableLiveData<List<MoveInfo>>()
    val pokemon = MutableLiveData<List<Pokemon>>()

    init {
        fetchTypes()
    }

    private val handler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            Log.e("COROUTINE_EXCEPTION", "$exception")
        }
    }

    private fun fetchTypes() {
        viewModelScope.launch {
            types.value = pokemonDao.getTypes()
        }
    }

    fun fetchPokemon(pokemonIdList: List<PokemonId>) {
        viewModelScope.launch(handler) {
            val pokemonFromDb = pokemonIdList.map {
                async {
                    pokemonDao.getSinglePokemon(it.name)
                }
            }.awaitAll()

            if (!pokemonFromDb.any { it == null } && pokemonIdList.size == pokemonFromDb.size) {
                pokemon.value = pokemonFromDb
                return@launch
            }

            val specieInfo: List<PokemonSpecie>
            val pokemonInfo: List<PokemonInfo>

            val fetchSpecieInfo = pokemonIdList.map { pokemonId ->
                async(Dispatchers.IO) {
                    apiService.getPokemonSpecieInfo(pokemonId.url.extractPokemonId())
                }
            }

            val fetchPokemonInfo = pokemonIdList.map { pokemonId ->
                async(Dispatchers.IO) {
                    apiService.getPokemonInfo(pokemonId.url.extractPokemonId())
                }
            }

            specieInfo = fetchSpecieInfo.awaitAll()
            pokemonInfo = fetchPokemonInfo.awaitAll()

            val pokemonList = mutableListOf<Pokemon>()

            for (i in pokemonIdList.indices) {
                pokemonList.add(
                    Pokemon(
                        pokemonInfo[i].id,
                        pokemonIdList[i],
                        specieInfo[i],
                        pokemonInfo[i]
                    )
                )
            }
            pokemon.value = pokemonList
        }
    }

    fun fetchMoves(ids: List<Int>) {
        viewModelScope.launch(handler) {
            moves.value = ids.map {
                async {
                    apiService.getMove(it)
                }
            }.awaitAll()
        }
    }
}