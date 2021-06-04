package com.rafaelboban.pokedex.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.FragmentSearchBinding
import com.rafaelboban.pokedex.databinding.LayoutSnackbarBinding
import com.rafaelboban.pokedex.ui.adapters.PokemonListAdapter
import com.rafaelboban.pokedex.ui.adapters.PokemonListLoadStateAdapter
import com.rafaelboban.pokedex.ui.viewmodels.SearchViewModel
import com.rafaelboban.pokedex.utils.ACTIVITY_STARTED_ID
import com.rafaelboban.pokedex.utils.Constants.EXTRA_POKEMON
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: PokemonListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        adapter = PokemonListAdapter(
            onPokemonClick = { pokemon ->
                ACTIVITY_STARTED_ID = pokemon.id
                val intent = Intent(context, PokemonActivity::class.java).apply {
                    putExtra(EXTRA_POKEMON, pokemon)
                }
                requireActivity().startActivity(intent)
            },
            onFavoriteClick = { pokemon ->
                viewModel.onFavoriteClick(pokemon)
            }
        )

        binding.apply {
            recyclerViewMain.itemAnimator = null
            recyclerViewMain.setHasFixedSize(false)
            recyclerViewMain.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewMain.adapter = adapter.withLoadStateHeaderAndFooter(
                header = PokemonListLoadStateAdapter { adapter.retry() },
                footer = PokemonListLoadStateAdapter { adapter.retry() }
            )
        }

        viewModel.setupLanguages()
        viewModel.fetchTypes()

        setupListeners()
        setupObservers()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyItemChanged(ACTIVITY_STARTED_ID)
    }

    private fun setupListeners() {

        val searchView = binding.toolbarSearch.searchview
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerViewMain.scrollToPosition(0)
                    viewModel.searchPokemon(query.trim())
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })


        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                adapter.loadStateFlow
                    .collect { loadState ->
                        val mediatorRefreshState = loadState.mediator?.refresh
                        val sourceRefreshState = loadState.source.refresh

                        if (mediatorRefreshState is LoadState.Error || sourceRefreshState is LoadState.Error) {
                            progressBarSearch.isVisible = false
                            recyclerViewMain.isVisible = adapter.itemCount > 0

                            val noCachedResults =
                                adapter.itemCount < 1 && loadState.source.append.endOfPaginationReached

                            errorStateSearch.root.isVisible = noCachedResults

                            if (noCachedResults) {
                                val sb = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
                                sb.view.setBackgroundColor(Color.TRANSPARENT);
                                val sbLayout = sb.view as Snackbar.SnackbarLayout
                                val sbBinding = LayoutSnackbarBinding.inflate(layoutInflater)
                                sbLayout.addView(sbBinding.root, 0)
                                sbBinding.message.text = getString(R.string.check_connection)
                                sbBinding.message.backgroundTintList =
                                    ColorStateList.valueOf(resources.getColor(R.color.error))
                                sbBinding.snackbarClose.setOnClickListener {
                                    sb.dismiss()
                                }
                                sb.show()
                            } else {
                                emptyStateSearch.root.isVisible = true
                            }
                        }

                        if (mediatorRefreshState is LoadState.NotLoading
                            && sourceRefreshState is LoadState.NotLoading
                            && loadState.append.endOfPaginationReached
                            && adapter.itemCount < 1
                        ) {
                            recyclerViewMain.isVisible = false
                            emptyStateSearch.root.isVisible = true
                            progressBarSearch.isVisible = false
                            errorStateSearch.root.isVisible = false
                        } else if (mediatorRefreshState is LoadState.NotLoading
                            && sourceRefreshState is LoadState.NotLoading
                            && adapter.itemCount == 0
                        ) {
                            recyclerViewMain.isVisible = false
                            emptyStateSearch.root.isVisible = false
                            progressBarSearch.isVisible = true
                            errorStateSearch.root.isVisible = false
                        } else if (mediatorRefreshState is LoadState.NotLoading
                            && sourceRefreshState is LoadState.NotLoading
                            && adapter.itemCount > 0
                        ) {
                            recyclerViewMain.isVisible = true
                            emptyStateSearch.root.isVisible = false
                            progressBarSearch.isVisible = false
                            errorStateSearch.root.isVisible = false
                        } else {
                            emptyStateSearch.root.isVisible = false
                        }
                    }
            }

            errorStateSearch.buttonRetry.setOnClickListener {
                adapter.retry()
            }
        }
    }

    private fun setupObservers() {
        viewModel.pokemon.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }
}