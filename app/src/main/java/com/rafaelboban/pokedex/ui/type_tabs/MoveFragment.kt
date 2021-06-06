package com.rafaelboban.pokedex.ui.type_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.pokedex.databinding.FragmentMoveBinding
import com.rafaelboban.pokedex.model.TypeFull
import com.rafaelboban.pokedex.ui.adapters.MovesAdapter
import com.rafaelboban.pokedex.ui.viewmodels.TypeViewModel
import com.rafaelboban.pokedex.utils.Constants.EXTRA_TYPE

class MoveFragment() : Fragment() {

    val viewModel: TypeViewModel by activityViewModels()
    lateinit var binding: FragmentMoveBinding
    lateinit var adapter: MovesAdapter
    lateinit var type: TypeFull

    var sortDirection = "ASC"
    var sortingByMarker = "GEN"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoveBinding.inflate(layoutInflater)
        adapter = MovesAdapter(listOf())

        val bundle = this.arguments
        if (bundle != null) type = bundle.getSerializable(EXTRA_TYPE) as TypeFull

        binding.recyclerView.adapter = adapter


        setupObservers()
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.headerGen.setOnClickListener {
            if (!viewModel.moves.value.isNullOrEmpty()) {
                var sorted = viewModel.moves.value!!.sortedBy {
                    it.generation.name.substringAfter(
                        "generation-"
                    ).uppercase()
                }
                if (sortingByMarker == "GEN" && sortDirection == "ASC") {
                    sorted = sorted.reversed()
                    sortDirection = "DESC"
                } else {
                    sortDirection = "ASC"
                }
                sortingByMarker = "GEN"
                adapter.setMovesList(sorted)
                binding.recyclerView.scrollToPosition(0)
            }
        }

        binding.headerMove.setOnClickListener {
            if (!viewModel.moves.value.isNullOrEmpty()) {
                var sorted = viewModel.moves.value!!.sortedBy {
                    it.name
                }
                if (sortingByMarker == "NAME" && sortDirection == "ASC") {
                    sorted = sorted.reversed()
                    sortDirection = "DESC"
                } else {
                    sortDirection = "ASC"
                }
                sortingByMarker = "NAME"
                adapter.setMovesList(sorted)
                binding.recyclerView.scrollToPosition(0)
            }
        }

        binding.headerPower.setOnClickListener {
            if (!viewModel.moves.value.isNullOrEmpty()) {
                var sorted = viewModel.moves.value!!.sortedBy {
                    it.power
                }
                if (sortingByMarker == "POWER" && sortDirection == "ASC") {
                    sorted = sorted.reversed()
                    sortDirection = "DESC"
                } else {
                    sortDirection = "ASC"
                }
                sortingByMarker = "POWER"
                adapter.setMovesList(sorted)
                binding.recyclerView.scrollToPosition(0)
            }
        }

        binding.headerPp.setOnClickListener {
            if (!viewModel.moves.value.isNullOrEmpty()) {
                var sorted = viewModel.moves.value!!.sortedBy {
                    it.pp
                }
                if (sortingByMarker == "PP" && sortDirection == "ASC") {
                    sorted = sorted.reversed()
                    sortDirection = "DESC"
                } else {
                    sortDirection = "ASC"
                }
                sortingByMarker = "PP"
                adapter.setMovesList(sorted)
                binding.recyclerView.scrollToPosition(0)
            }
        }

        binding.headerCategory.setOnClickListener {
            if (!viewModel.moves.value.isNullOrEmpty()) {
                var sorted = viewModel.moves.value!!.sortedBy {
                    if (it.damage_class != null)
                        it.damage_class.name
                    else it.name
                }
                if (sortingByMarker == "CATEGORY" && sortDirection == "ASC") {
                    sorted = sorted.reversed()
                    sortDirection = "DESC"
                } else {
                    sortDirection = "ASC"
                }
                sortingByMarker = "CATEGORY"
                adapter.setMovesList(sorted)
                binding.recyclerView.scrollToPosition(0)
            }
        }
    }

    private fun setupObservers() {
        viewModel.moves.observe(viewLifecycleOwner) {
            adapter.setMovesList(it)
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkEmpty()
            }

            fun checkEmpty() {
                binding.apply {
                    progressBar.visibility =
                        if (adapter.itemCount == 0) View.VISIBLE else View.GONE
                    recyclerView.visibility =
                        if (adapter.itemCount > 0) View.VISIBLE else View.GONE

                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(type: TypeFull): MoveFragment {
            val moveFragment = MoveFragment()
            val bundle = Bundle().apply { putSerializable(EXTRA_TYPE, type) }
            moveFragment.arguments = bundle
            return moveFragment
        }
    }
}