package com.rafaelboban.pokedex.ui.viewmodels

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.rafaelboban.pokedex.data.PokemonRepository
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.PokemonId
import com.rafaelboban.pokedex.utils.extractId
import com.rafaelboban.pokedex.utils.filterAll
import com.rafaelboban.pokedex.utils.transformToRange
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val pokemonDao: PokemonDao
    ) : ViewModel() {

    private val currentQuery = MutableLiveData("")

    val pokemon = currentQuery.switchMap { query ->
        repository.getPokemon().map { pagingData ->
            pagingData.filterAll(generateFilters(query))
        }.cachedIn(viewModelScope)
    }

    fun searchPokemon(query: String) {
        currentQuery.value = query
    }
}

private fun generateFilters(query: String) = listOf<(PokemonId) -> Boolean>(
    { query.lowercase() in it.name.lowercase() },
    { query == it.id.toString().padStart(3, '0') },
    { val range = query.transformToRange()
        if (range != null) {
            it.url.extractId() in range
        } else {
            false
        }
    }
)