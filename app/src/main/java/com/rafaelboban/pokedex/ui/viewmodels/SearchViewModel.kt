package com.rafaelboban.pokedex.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rafaelboban.pokedex.api.di.RetrofitBuilder
import com.rafaelboban.pokedex.data.PokemonRepository
import com.rafaelboban.pokedex.model.PokemonId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: PokemonRepository) : ViewModel() {
    private val currentQuery = MutableLiveData("")

    val pokemon = currentQuery.switchMap { query ->
        repository.getPokemon(query).cachedIn(viewModelScope)
    }

    fun searchPokemon(query: String) {
        currentQuery.value = query
    }
}