package com.rafaelboban.pokedex.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.pokedex.database.PokemonDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val pokemonDao: PokemonDao) : ViewModel() {

    fun clearFavorites() {
        viewModelScope.launch {
            pokemonDao.clear()
        }
    }
}