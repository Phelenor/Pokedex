package com.rafaelboban.pokedex.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.TypeFull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TypeViewModel @Inject constructor(
    private val apiService: ApiService,
    val pokemonDao: PokemonDao
) : ViewModel() {

    val types = MutableLiveData<List<TypeFull>>()

    init {
        viewModelScope.launch {
            types.value = pokemonDao.getTypes()
        }
    }
}