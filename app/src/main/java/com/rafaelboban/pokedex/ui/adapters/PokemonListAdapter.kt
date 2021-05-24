package com.rafaelboban.pokedex.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.databinding.CardPokemonItemBinding
import com.rafaelboban.pokedex.model.PokemonId
import com.rafaelboban.pokedex.utils.getSprite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PokemonListAdapter(val pokemonDao: PokemonDao, val isFavoritesAdapter: Boolean = false) :
    PagingDataAdapter<PokemonId, PokemonListAdapter.PokemonViewHolder>(POKEMON_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding =
            CardPokemonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val current = getItem(position)
        if (current != null) {
            holder.bind(current)
        }
    }

    inner class PokemonViewHolder(private val binding: CardPokemonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: PokemonId) {
            binding.apply {
                pokemonImage.load(pokemon.getSprite()) {
                    listener(
                        onStart = {
                            imageLoadProgressbar.visibility = View.VISIBLE
                            pokemonImage.visibility = View.GONE
                            imagePlaceholder.visibility = View.GONE
                        },
                        onSuccess = { _, _ ->
                            imageLoadProgressbar.visibility = View.GONE
                            pokemonImage.visibility = View.VISIBLE
                            imagePlaceholder.visibility = View.GONE
                        },
                        onError = { _, _ ->
                            imageLoadProgressbar.visibility = View.GONE
                            pokemonImage.visibility = View.GONE
                            imagePlaceholder.visibility = View.VISIBLE
                        }

                    )
                }

                if (pokemon.isFavorite) {
                    favoriteButton.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            binding.root.resources,
                            R.drawable.ic_star_1, null
                        )
                    )
                } else {
                    favoriteButton.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            binding.root.resources,
                            R.drawable.ic_star_0, null
                        )
                    )
                }

                pokemonName.text = pokemon.name.capitalize()
                pokemonId.text = pokemon.pokemonId.toString().padStart(3, '0')

                favoriteButton.setOnClickListener {
                    if (pokemon.isFavorite) {
                        pokemon.isFavorite = false
                        pokemon.id = null

                        CoroutineScope(Dispatchers.IO).launch {
                            pokemonDao.delete(pokemon.name)
                            Log.d("FAVS", pokemonDao.getFavorites().map {
                                it.name to it.id
                            }.toString())
                        }

                        binding.favoriteButton.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                binding.root.resources,
                                R.drawable.ic_star_0, null
                            )
                        )
                    } else {
                        pokemon.isFavorite = true

                        CoroutineScope(Dispatchers.IO).launch {
                            pokemonDao.insert(pokemon)
                            Log.d("FAVS", pokemonDao.getFavorites().map {
                                it.name to it.id
                            }.toString())
                        }

                        binding.favoriteButton.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                binding.root.resources,
                                R.drawable.ic_star_1, null
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val POKEMON_COMPARATOR = object : DiffUtil.ItemCallback<PokemonId>() {
            override fun areItemsTheSame(oldItem: PokemonId, newItem: PokemonId): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: PokemonId, newItem: PokemonId): Boolean =
                oldItem == newItem
        }
    }
}