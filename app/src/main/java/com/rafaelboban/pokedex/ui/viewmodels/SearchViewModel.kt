package com.rafaelboban.pokedex.ui.viewmodels

import androidx.lifecycle.*
import androidx.paging.*
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.data.PokemonPagingSource
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.lang.LanguageId
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.filterAll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

const val NETWORK_PAGE_SIZE = 20

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val pokemonDao: PokemonDao,
    private val apiService: ApiService
) : ViewModel() {

    private val currentQuery = MutableLiveData("")
    private val rangeRegex = "^[0-9]+(-([0-9])+)*\$".toRegex()

    val pokemon = currentQuery.switchMap {
        var start = 0
        var end = 900
        var query = it.trim()

        if (query.isNotBlank() && query[query.lastIndex] == '-') query = query.substring(0 until query.lastIndex)

        if (rangeRegex.matches(query)) {
            val range = query.split('-')
            start = range[0].toInt() - 1
            if (range.size > 1) end = range[1].toInt()
        }

        getPagedData(start, end).map { pagingData ->
            pagingData.filterAll(generateFilters(query.lowercase()))
        }.cachedIn(viewModelScope)
    }

    private fun getPagedData(start: Int = 0, end: Int = 900): LiveData<PagingData<Pokemon>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PokemonPagingSource(apiService, pokemonDao, start, end) }
        ).liveData
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

    private fun generateFilters(query: String) = listOf<(Pokemon) -> Boolean>(
        { query.isBlank() },
        {
            query in it.specieClass.names.map { translation ->
                translation.name.lowercase()
            }
        },
        { query in it.idClass.name },
        { query == it.idClass.pokemonId.toString().padStart(3, '0') },

        {
            query in it.infoClass.types.map { typeInfo ->
            typeInfo.type.name
          }
        },

        {
            var start = 0
            var end = 900
            if (rangeRegex.matches(query)) {
                val range = query.split('-')
                start = range[0].toInt()
                if (range.size > 1) end = range[1].toInt()

                it.idClass.pokemonId in start..end
            } else {
                false
            }
        }

    )
}