package com.rafaelboban.pokedex.ui

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.databinding.FragmentSearchBinding
import com.rafaelboban.pokedex.databinding.LayoutSnackbarBinding
import com.rafaelboban.pokedex.ui.adapters.PokemonListAdapter
import com.rafaelboban.pokedex.ui.adapters.PokemonListLoadStateAdapter
import com.rafaelboban.pokedex.ui.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: PokemonListAdapter

    @Inject
    lateinit var db: PokemonDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        adapter = PokemonListAdapter(db)

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


        setupListeners()
        setupObservers()
        return binding.root
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

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBarSearch.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerViewMain.isVisible = loadState.source.refresh is LoadState.NotLoading
                errorStateSearch.root.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.Error) {
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
                }

                if (loadState.source.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached && adapter.itemCount < 1
                ) {
                    recyclerViewMain.isVisible = false
                    emptyStateSearch.root.isVisible = true
                } else {
                    emptyStateSearch.root.isVisible = false
                }
            }
        }

        binding.errorStateSearch.buttonRetry.setOnClickListener {
            adapter.retry()
        }

    }

    private fun setupObservers() {
        viewModel.pokemon.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }
}