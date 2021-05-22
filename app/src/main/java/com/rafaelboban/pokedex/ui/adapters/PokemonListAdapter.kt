package com.rafaelboban.pokedex.ui.adapters

import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rafaelboban.pokedex.databinding.CardPokemonItemBinding
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.PokemonId
import com.rafaelboban.pokedex.utils.extractId
import com.rafaelboban.pokedex.utils.getSprite

class PokemonListAdapter : PagingDataAdapter<PokemonId, PokemonListAdapter.PokemonViewHolder>(POKEMON_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = CardPokemonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val current = getItem(position)

        if (current != null) {
            holder.bind(current)
        }
    }

    class PokemonViewHolder(private val binding: CardPokemonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(pokemon: PokemonId) {
                binding.apply {
                    pokemonImage.load(pokemon.getSprite()) {
                        listener(
                            onStart = {
                                imageLoadProgressbar.visibility = View.VISIBLE
                                pokemonImage.visibility = View.GONE
                            },
                            onSuccess = { _, _ ->
                                imageLoadProgressbar.visibility = View.GONE
                                pokemonImage.visibility = View.VISIBLE
                            }
                        )
                    }
                    pokemonName.text = pokemon.name.capitalize()
                    pokemonId.text = pokemon.url.extractId().toString().padStart(3, '0')
                }
            }
    }

    companion object {
        private val POKEMON_COMPARATOR = object  : DiffUtil.ItemCallback<PokemonId>() {
            override fun areItemsTheSame(oldItem: PokemonId, newItem: PokemonId): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: PokemonId, newItem: PokemonId): Boolean =
                oldItem == newItem
        }
    }
}