package com.rafaelboban.pokedex.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.lang.LanguageId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val pokemonDao: PokemonDao
) : ViewModel() {
    val languages = MutableLiveData<List<LanguageId>>()

    init {
        languages.value = mutableListOf()
        getLanguages()
    }

    fun clearFavorites() {
        viewModelScope.launch {
            pokemonDao.clearFavorites()
            pokemonDao.deleteFavoriteStatus()
        }
    }

    private fun getLanguages() {
        viewModelScope.launch {
            val response = async { pokemonDao.getLanguages() }
            languages.value = response.await()
        }
    }
}
