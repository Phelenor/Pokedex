package com.rafaelboban.pokedex.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafaelboban.pokedex.databinding.FragmentSearchBinding
import com.rafaelboban.pokedex.ui.adapters.PokemonListAdapter
import com.rafaelboban.pokedex.ui.adapters.PokemonListLoadStateAdapter
import com.rafaelboban.pokedex.ui.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

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
        adapter = PokemonListAdapter()

        binding.apply {
            recyclerViewMain.setHasFixedSize(false)
            recyclerViewMain.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewMain.adapter = adapter.withLoadStateHeaderAndFooter(
                header = PokemonListLoadStateAdapter { adapter.retry() },
                footer = PokemonListLoadStateAdapter { adapter.retry() }
            )
        }
        setupListeners()
        setupObservers()
        return binding.root
    }


    private fun setupListeners() {
        val searchView = binding.toolbarSearch.searchview as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerViewMain.scrollToPosition(0)
                    viewModel.searchPokemon(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun setupObservers() {
        viewModel.pokemon.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }
}