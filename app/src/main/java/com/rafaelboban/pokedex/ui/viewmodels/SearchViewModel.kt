package com.rafaelboban.pokedex.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rafaelboban.pokedex.api.RetrofitBuilder
import com.rafaelboban.pokedex.data.PokemonRepository
import com.rafaelboban.pokedex.model.Pokemon

class SearchViewModel() : ViewModel() {
    private val repository = PokemonRepository(RetrofitBuilder.apiService)
    private var pokemonListCurrent: LiveData<PagingData<Pokemon>>? = null

    init {
        getPokemon()
    }


    fun getPokemon(): LiveData<PagingData<Pokemon>> {
        val lastResult = pokemonListCurrent
        if (lastResult != null) {
            return lastResult
        }
        val newResult = repository.getPokemon().cachedIn(viewModelScope)
        pokemonListCurrent = newResult
        return newResult
    }


}