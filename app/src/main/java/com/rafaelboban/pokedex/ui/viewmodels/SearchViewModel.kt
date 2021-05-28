package com.rafaelboban.pokedex.ui.viewmodels

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.data.PokemonRepository
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.PokemonId
import com.rafaelboban.pokedex.model.lang.LanguageId
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.extractPokemonId
import com.rafaelboban.pokedex.utils.filterAll
import com.rafaelboban.pokedex.utils.transformToRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val pokemonDao: PokemonDao,
    private val apiService: ApiService
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

    fun setupLanguages() {
        viewModelScope.launch {
            var languages = pokemonDao.getLanguages()
            if (languages.isEmpty()) {
                languages = apiService.getLanguages().results as MutableList<LanguageId>
                val fetchLanguageData = languages.map { lang ->
                    async {
                        apiService.getLanguageInfo(lang.url.extractLangId())
                    }
                }
                val languagesInfo = fetchLanguageData.awaitAll()
                val toBeRemoved: MutableList<LanguageId> = mutableListOf()
                findEngName@ for (lang in languages) {
                    for (langInfo in languagesInfo) {
                        if (lang.name == langInfo.name) {
                            for (nameForeign in langInfo.names) {
                                if (nameForeign.language.name == "en") {
                                    lang.nameEnglish = nameForeign.name
                                    continue@findEngName
                                }
                                if (nameForeign.language.name == lang.name) {
                                    lang.nameNative = nameForeign.name
                                }
                            }
                        }
                    }
                    if (lang.nameEnglish == null && lang.nameNative == null) {
                        toBeRemoved.add(lang)
                    }
                }

                languages.removeAll(toBeRemoved)
                pokemonDao.insertLanguages(languages)
            }
        }
    }

    private fun generateFilters(query: String) = listOf<(PokemonId) -> Boolean>(
        { query.lowercase() in it.name.lowercase() },
        { query == it.id.toString().padStart(3, '0') },
        {
            val range = query.transformToRange()
            if (range != null) {
                it.url.extractPokemonId() in range
            } else {
                false
            }
        }
    )
}