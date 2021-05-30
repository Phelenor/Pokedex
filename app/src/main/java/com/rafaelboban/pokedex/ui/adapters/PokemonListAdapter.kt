package com.rafaelboban.pokedex.ui.adapters

import android.content.Context
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
import com.rafaelboban.pokedex.model.Favorite
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.getSprite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PokemonListAdapter(val pokemonDao: PokemonDao) :
    PagingDataAdapter<Pokemon, PokemonListAdapter.PokemonViewHolder>(POKEMON_COMPARATOR) {

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

        fun bind(pokemon: Pokemon) {
            val pokemonBase = pokemon.idClass
            binding.apply {
                pokemonImage.load(pokemonBase.getSprite()) {
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

                if (pokemonBase.isFavorite) {
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
                val sp = root.context.getSharedPreferences("default", Context.MODE_PRIVATE)
                val langId = sp.getInt("langId", 0)

                if (sp.getInt("langId", 0) == 9) {
                    pokemonName.text = pokemonBase.name.capitalize()
                } else {
                    for (name in pokemon.specieClass.names) {
                        if (name.language.url.extractLangId() == langId) {
                            pokemonName.text = name.name.capitalize()
                            break
                        }
                    }
                }

                pokemonId.text = pokemonBase.pokemonId.toString().padStart(3, '0')

                favoriteButton.setOnClickListener {
                    if (pokemonBase.isFavorite) {
                        pokemonBase.isFavorite = false
                        pokemonBase.id = null

                        CoroutineScope(Dispatchers.IO).launch {
                            pokemonDao.delete(pokemonBase.name)
                        }

                        binding.favoriteButton.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                binding.root.resources,
                                R.drawable.ic_star_0, null
                            )
                        )
                    } else {
                        pokemonBase.isFavorite = true
                        CoroutineScope(Dispatchers.IO).launch {
                            pokemonDao.insert(Favorite(pokemon = pokemon))
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
        private val POKEMON_COMPARATOR = object : DiffUtil.ItemCallback<Pokemon>() {
            override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean =
                oldItem.idClass.name == newItem.idClass.name

            override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean =
                oldItem == newItem
        }
    }
}