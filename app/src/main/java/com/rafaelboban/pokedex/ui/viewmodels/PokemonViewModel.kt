package com.rafaelboban.pokedex.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.*
import com.rafaelboban.pokedex.utils.extractEvolutionId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val apiService: ApiService,
    val pokemonDao: PokemonDao
) : ViewModel() {

    val evolutions = MutableLiveData<List<List<Pair<Pokemon, Int>>>>()
    val types = MutableLiveData<List<TypeFull>>()

    init {
        viewModelScope.launch {
            types.value = pokemonDao.getTypes()
        }
    }

    private val handler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            Log.e("COROUTINE_EXCEPTION", "$exception")
        }
    }

    fun onFavoriteClick(pokemon: Pokemon) {
        viewModelScope.launch {
            if (pokemon.idClass.isFavorite) {
                pokemon.idClass.isFavorite = false
                pokemonDao.deleteFavorite(pokemon.idClass.name)
            } else {
                pokemon.idClass.isFavorite = true
                pokemonDao.insertFavorite(Favorite(pokemon = pokemon))
            }
            pokemonDao.updatePokemon(pokemon.id, (if (pokemon.idClass.isFavorite) 1 else 0))
        }
    }

    fun getEvolutionChain(pokemon: Pokemon) {
        viewModelScope.launch(handler) {
            val data =
                apiService.getEvolutionChain(pokemon.specieClass.evolution_chain.url.extractEvolutionId())
            val extracted = extractEvolutions(data.chain, pokemon)
            evolutions.value = extracted.map {
                it.map { pair ->
                    var pokemonEvolved = pokemonDao.getSinglePokemon(pair.first)

                    val specieFetch = async {
                        apiService.getPokemonSpecieByName(pair.first)
                    }

                    val infoFetch = async {
                        apiService.getPokemonByName(pair.first)
                    }

                    if (pokemonEvolved == null) {
                        pokemonEvolved = Pokemon(
                            0,
                            PokemonId("", ""),
                            specieFetch.await(),
                            infoFetch.await()
                        )
                    }
                    Pair(pokemonEvolved, pair.second)
                }
            }
        }
    }

    private fun extractEvolutions(
        chain: Chain,
        pokemon: Pokemon
    ): MutableList<MutableList<Pair<String, Int>>> {
        var mainChain: Chain = chain

        if (chain.species.name == pokemon.idClass.name) {
            mainChain = chain
        } else {
            outer@ for (evolutionChain in chain.evolves_to) {
                if (evolutionChain.species.name == pokemon.idClass.name) {
                    mainChain = evolutionChain
                    break
                } else {
                    for (evolutionChainInner in evolutionChain.evolves_to) {
                        if (evolutionChainInner.species.name == pokemon.idClass.name) {
                            mainChain = evolutionChainInner
                            break@outer
                        }
                    }
                }
            }
        }

        return findAllChains(mainChain, mutableListOf(), mutableListOf())
    }

    private fun findAllChains(
        chain: Chain,
        list: MutableList<Pair<String, Int>>,
        master: MutableList<MutableList<Pair<String, Int>>>
    ): MutableList<MutableList<Pair<String, Int>>> {
        val local = list.toMutableList()
        if (chain.evolution_details.isNotEmpty()) {
            local.add(Pair(chain.species.name, chain.evolution_details[0].min_level))
        } else {
            local.add(Pair(chain.species.name, 0))
        }

        if (chain.evolves_to.isEmpty()) {
            master.add(local)
        } else {
            for (evolution in chain.evolves_to) {
                findAllChains(evolution, local, master)
            }
        }
        return master
    }
}
