package com.rafaelboban.pokedex.ui.type_tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.FragmentPokemonBinding
import com.rafaelboban.pokedex.model.TypeFull
import com.rafaelboban.pokedex.ui.PokemonActivity
import com.rafaelboban.pokedex.ui.adapters.TypePokemonAdapter
import com.rafaelboban.pokedex.ui.viewmodels.TypeViewModel
import com.rafaelboban.pokedex.utils.ACTIVITY_STARTED_ID
import com.rafaelboban.pokedex.utils.Constants
import com.rafaelboban.pokedex.utils.ItemOffsetDecoration


class PokemonFragment(val type: TypeFull) : Fragment() {

    lateinit var binding: FragmentPokemonBinding
    private val viewModel: TypeViewModel by activityViewModels()
    private lateinit var adapter: TypePokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPokemonBinding.inflate(layoutInflater)

        adapter = TypePokemonAdapter(listOf(),
        onPokemonClick = { pokemon ->
            ACTIVITY_STARTED_ID = pokemon.id
            val intent = Intent(requireContext(), PokemonActivity::class.java).apply {
                putExtra(Constants.EXTRA_POKEMON, pokemon)
            }
            startActivity(intent)
        })

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.item_offset))

        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.pokemon.observe(viewLifecycleOwner) {
            adapter.setPokemon(it)
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(type: TypeFull) = PokemonFragment(type)
    }
}