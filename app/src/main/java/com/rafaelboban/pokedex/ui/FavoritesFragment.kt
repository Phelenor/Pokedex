package com.rafaelboban.pokedex.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.pokedex.databinding.FragmentFavoritesBinding
import com.rafaelboban.pokedex.ui.adapters.FavoritesAdapter
import com.rafaelboban.pokedex.ui.viewmodels.FavoritesViewModel
import com.rafaelboban.pokedex.utils.ACTIVITY_STARTED_ID
import com.rafaelboban.pokedex.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel by viewModels<FavoritesViewModel>()
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FavoritesAdapter

    private val itemTouchHelper by lazy {
        val itemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                UP or
                        DOWN or
                        START or
                        END, 0
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val adapter = recyclerView.adapter as FavoritesAdapter
                    val from = viewHolder.bindingAdapterPosition
                    val to = target.bindingAdapterPosition
                    adapter.moveItem(from, to)
                    adapter.notifyItemMoved(from, to)
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                }
            }
        ItemTouchHelper(itemTouchCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        adapter = FavoritesAdapter(mutableListOf(),
            onFavoriteClick = { favorite ->
                viewModel.deleteFavorite(favorite)
            },
            onFavoritesEdit = { holder ->
                this.startDragging(holder)
                true
            },
            onPokemonClick = { pokemon ->
                ACTIVITY_STARTED_ID = pokemon.id
                val intent = Intent(context, PokemonActivity::class.java).apply {
                    putExtra(Constants.EXTRA_POKEMON, pokemon)
                }
                requireActivity().startActivity(intent)
            }
        )


        binding.apply {
            recyclerViewFavorites.setHasFixedSize(false)
            recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewFavorites.adapter = adapter
        }


        setupListeners()
        setupObservers()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyItemChanged(ACTIVITY_STARTED_ID)
    }

    private fun setupObservers() {
        viewModel.favoritePokemon.observe(viewLifecycleOwner, {
            adapter.apply {
                adapter.setPokemon(it)
                adapter.notifyDataSetChanged()
            }
        })

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
                    emptyStateFavorites.root.visibility =
                        if (adapter.itemCount == 0) View.VISIBLE else View.GONE

                    toolbarFavorites.buttonEdit.visibility =
                        if (adapter.itemCount != 0 && !adapter.favoritesModeEdit) View.VISIBLE
                        else View.GONE

                    toolbarFavorites.buttonDone.visibility =
                        if (adapter.itemCount != 0 && adapter.favoritesModeEdit) View.VISIBLE
                        else {
                            adapter.favoritesModeEdit = false
                            View.GONE
                        }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupListeners() {
        binding.toolbarFavorites.buttonEdit.setOnClickListener {
            binding.toolbarFavorites.buttonEdit.visibility = View.GONE
            binding.toolbarFavorites.buttonDone.visibility = View.VISIBLE
            adapter.favoritesModeEdit = true
            itemTouchHelper.attachToRecyclerView(binding.recyclerViewFavorites)
            adapter.notifyDataSetChanged()
        }

        binding.toolbarFavorites.buttonDone.setOnClickListener {
            binding.toolbarFavorites.buttonDone.visibility = View.GONE
            binding.toolbarFavorites.buttonEdit.visibility = View.VISIBLE
            itemTouchHelper.attachToRecyclerView(null)
            adapter.favoritesModeEdit = false
            adapter.notifyDataSetChanged()

            viewModel.updateFavorites(adapter.favorites)
        }
    }

    private fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }
}