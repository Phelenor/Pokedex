package com.rafaelboban.pokedex.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.Favorite
import com.rafaelboban.pokedex.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val pokemonDao: PokemonDao) : ViewModel() {


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
}
