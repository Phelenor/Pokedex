package com.rafaelboban.pokedex.ui.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.rafaelboban.pokedex.api.ApiService
import com.rafaelboban.pokedex.data.PokemonRemoteMediator
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.database.RemoteKeysDao
import com.rafaelboban.pokedex.model.Favorite
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.UiModel
import com.rafaelboban.pokedex.model.lang.LanguageId
import com.rafaelboban.pokedex.utils.Constants.LANGUAGE_KEY
import com.rafaelboban.pokedex.utils.Constants.NETWORK_PAGE_SIZE
import com.rafaelboban.pokedex.utils.Constants.POKEMON_LIST_SIZE
import com.rafaelboban.pokedex.utils.extractGeneration
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.filterAll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val pokemonDao: PokemonDao,
    private val apiService: ApiService,
    private val remoteKeysDao: RemoteKeysDao,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val currentQuery = MutableLiveData("")
    private val rangeRegex = "^[0-9]+(-([0-9])+)*\$".toRegex()

    private val handler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            Log.e("COROUTINE_EXCEPTION", "$exception")
        }
    }

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
                remoteKeysDao,
                sharedPreferences
            ),
            pagingSourceFactory = { pokemonDao.getPokemon() }
        ).liveData
    }

    fun searchPokemon(query: String) {
        currentQuery.value = query
    }

    private fun generateFilters(query: String) = listOf<(Pokemon) -> Boolean>(
        { query.isBlank() },
        { pokemon ->
            val lang = sharedPreferences.getInt(LANGUAGE_KEY, 9)
            val names = pokemon.specieClass.names.filter {
                it.language.url.extractLangId() == lang
            }.map { translation ->
                translation.name.lowercase()
            }

            for (name in names) {
                if (name.startsWith(query)) return@listOf true
            }
            false
        },

        { it.idClass.name.lowercase().startsWith(query.lowercase()) },
        { query == it.infoClass.id.toString().padStart(3, '0') },
        {
            query in it.infoClass.types.map { typeInfo ->
                typeInfo.type.name
            }
        },
        {
            var currentQuery = query
            var end = POKEMON_LIST_SIZE
            if (query.isNotBlank() && query[query.lastIndex] == '-') currentQuery =
                query.substring(0 until query.lastIndex)
            if (rangeRegex.matches(currentQuery)) {
                val range = currentQuery.split('-')
                val start = range[0].toInt()
                if (range.size > 1) end = range[1].toInt()

                it.infoClass.id in start..end
            } else {
                false
            }
        }
    )

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
            pokemonDao.insertPokemon(listOf(pokemon))
        }
    }

    fun setupLanguages() {
        viewModelScope.launch(handler) {
            var languages = pokemonDao.getLanguages()
            if (languages.isEmpty()) {
                Log.d("SETUP", "lang setup")
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

    fun fetchTypes() {
        viewModelScope.launch {
            if (pokemonDao.getTypes().isEmpty()) {
                val types = (1..18).map {
                    async {
                        apiService.getTypeFull(it)
                    }
                }.awaitAll()
                Log.d("TYPES", types.map {it.pokemon }.toString())
                pokemonDao.insertTypes(types)
            }
        }
    }
}

private val UiModel.PokemonItem.generation: Int
    get() = this.pokemon.specieClass.generation.url.extractGeneration()