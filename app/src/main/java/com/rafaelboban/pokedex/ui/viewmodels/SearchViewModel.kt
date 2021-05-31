package com.rafaelboban.pokedex.ui.viewmodels

import androidx.lifecycle.*
import androidx.paging.*
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.data.PokemonRemoteMediator
import com.rafaelboban.pokedex.data.RemoteKeysDao
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.UiModel
import com.rafaelboban.pokedex.model.lang.LanguageId
import com.rafaelboban.pokedex.utils.extractGeneration
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
    private val apiService: ApiService,
    private val remoteKeysDao: RemoteKeysDao
) : ViewModel() {

    private val currentQuery = MutableLiveData("")
    private val rangeRegex = "^[0-9]+(-([0-9])+)*\$".toRegex()

    val pokemon = currentQuery.switchMap { query ->

        getPagedData()
            .map { pagingData -> pagingData.filterAll(generateFilters(query.lowercase())) }
            .map { pagingData -> pagingData.map { UiModel.PokemonItem(it) } }
            .map { pagingData ->
                pagingData.insertSeparators { before, after ->
                    if (after == null) {
                        return@insertSeparators null
                    }

                    if (before == null) {
                        return@insertSeparators UiModel.SeparatorItem(after.generation.toString())
                    }

                    if (before.generation < after.generation) {
                        UiModel.SeparatorItem(after.generation.toString())
                    } else {
                        null
                    }
                }
            }.cachedIn(viewModelScope)
    }

    private fun getPagedData(): LiveData<PagingData<Pokemon>> {

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = PokemonRemoteMediator(
                apiService,
                pokemonDao,
                remoteKeysDao
            ),
            pagingSourceFactory = { pokemonDao.getPokemon() }
        ).liveData
    }

    fun searchPokemon(query: String) {
        currentQuery.value = query
    }

    private fun generateFilters(query: String) = listOf<(Pokemon) -> Boolean>(
        { query.isBlank() },
        {
            val names = it.specieClass.names.map { translation ->
                translation.name.lowercase()
            }

            for (name in names) {
                if (name.startsWith(query)) return@listOf true
            }
            false
        },

        { it.idClass.name.startsWith(query) },
        { query == it.idClass.pokemonId.toString().padStart(3, '0') },
        {
            query in it.infoClass.types.map { typeInfo ->
                typeInfo.type.name
            }
        },
        {
            var currentQuery = query
            var end = 898
            if (query.isNotBlank() && query[query.lastIndex] == '-') currentQuery =
                query.substring(0 until query.lastIndex)
            if (rangeRegex.matches(currentQuery)) {
                val range = currentQuery.split('-')
                val start = range[0].toInt()
                if (range.size > 1) end = range[1].toInt()

                it.idClass.pokemonId in start..end
            } else {
                false
            }
        }
    )

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
}

private val UiModel.PokemonItem.generation: Int
    get() = this.pokemon.specieClass.generation.url.extractGeneration()