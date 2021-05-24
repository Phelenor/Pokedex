package com.rafaelboban.pokedex.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.databinding.FragmentFavoritesBinding
import com.rafaelboban.pokedex.ui.adapters.FavoritesAdapter
import com.rafaelboban.pokedex.ui.viewmodels.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel by viewModels<FavoritesViewModel>()
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FavoritesAdapter

    @Inject
    lateinit var db: PokemonDao

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
                ) {}
            }
        ItemTouchHelper(itemTouchCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        adapter = FavoritesAdapter(mutableListOf(), db, this)

        binding.apply {
            recyclerViewFavorites.setHasFixedSize(false)
            recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewFavorites.adapter = adapter
        }

        setupListeners()
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.favoritePokemon.observe(viewLifecycleOwner, {
            adapter.apply {
                adapter.setPokemon(it)
                notifyDataSetChanged()
            }
        })

//        viewModel.status.observe(viewLifecycleOwner, {
//            val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
//            snackbar.view.setBackgroundColor(Color.TRANSPARENT);
//            val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
//            snackbarLayout.setPadding(0, 0, 0, 0);
//            val snackBinding = SnackbarBinding.inflate(LayoutInflater.from(context))
//            if (!it) {
//                snackBinding.snackbarClose.setOnClickListener {
//                    snackbar.dismiss()
//                }
//                snackBinding.message.text = getString(R.string.network_error)
//                snackBinding.message.backgroundTintList = ColorStateList.valueOf(
//                    resources.getColor(
//                        R.color.error
//                    )
//                )
//                snackbarLayout.addView(snackBinding.root, 0)
//                snackbar.show()
//            }
//        })
    }

    private fun setupListeners() {
        binding.toolbarFavorites.buttonEdit.setOnClickListener {
            binding.toolbarFavorites.buttonEdit.visibility = View.GONE
            binding.toolbarFavorites.buttonDone.visibility = View.VISIBLE
            adapter.FAVORITES_EDIT_MODE = true
            itemTouchHelper.attachToRecyclerView(binding.recyclerViewFavorites)
            adapter.notifyDataSetChanged()
        }

        binding.toolbarFavorites.buttonDone.setOnClickListener {
            binding.toolbarFavorites.buttonDone.visibility = View.GONE
            binding.toolbarFavorites.buttonEdit.visibility = View.VISIBLE
            itemTouchHelper.attachToRecyclerView(null)
            adapter.FAVORITES_EDIT_MODE = false
            adapter.notifyDataSetChanged()

            viewModel.updateFavorites(adapter.pokemonList)
        }
    }

    fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }
}