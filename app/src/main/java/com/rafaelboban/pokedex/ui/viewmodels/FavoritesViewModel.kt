package com.rafaelboban.pokedex.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.Favorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(val pokemonDao: PokemonDao) : ViewModel() {

    val favoritePokemon = MutableLiveData<List<Favorite>>()

    init {
        favoritePokemon.value = mutableListOf()
        getFavorites()
    }

    private fun getFavorites() {
        viewModelScope.launch {
            favoritePokemon.value = pokemonDao.getFavorites()
        }
    }

    fun updateFavorites(pokemonList: List<Favorite>) {
        viewModelScope.launch {
            pokemonDao.clear()
            for (i in pokemonList.indices) {
                pokemonList[i].id = i + 1
                pokemonDao.insert(pokemonList[i])
            }
        }
    }
}