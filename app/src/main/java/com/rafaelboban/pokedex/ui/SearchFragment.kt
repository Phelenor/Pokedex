package com.rafaelboban.pokedex.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.pokedex.databinding.FragmentSearchBinding
import com.rafaelboban.pokedex.ui.adapters.PokemonListAdapter
import com.rafaelboban.pokedex.ui.adapters.PokemonListLoadStateAdapter
import com.rafaelboban.pokedex.ui.viewmodels.SearchViewModel

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

        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.getPokemon().observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }
}