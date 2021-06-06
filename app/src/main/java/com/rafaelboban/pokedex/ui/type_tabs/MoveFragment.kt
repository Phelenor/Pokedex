package com.rafaelboban.pokedex.ui.type_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rafaelboban.pokedex.databinding.FragmentMoveBinding
import com.rafaelboban.pokedex.model.TypeFull
import com.rafaelboban.pokedex.ui.adapters.MovesAdapter
import com.rafaelboban.pokedex.ui.viewmodels.TypeViewModel

class MoveFragment(val type: TypeFull) : Fragment() {

    val viewModel: TypeViewModel by activityViewModels()
    lateinit var binding: FragmentMoveBinding
    lateinit var adapter: MovesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoveBinding.inflate(layoutInflater)
        adapter = MovesAdapter(viewModel.moves.value!!)

        binding.recyclerView.adapter = adapter

        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.moves.observe(viewLifecycleOwner) {
            adapter.setMovesList(it)
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(type: TypeFull) = MoveFragment(type)
    }
}