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

class PokemonListAdapter : PagingDataAdapter<Pokemon, PokemonListAdapter.PokemonViewHolder>(POKEMON_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = CardPokemonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val current = getItem(position)

        if (current != null) {
            current.id = position + 1
            holder.bind(current)
        }
    }

    class PokemonViewHolder(private val binding: CardPokemonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(pokemon: Pokemon) {

                binding.apply {

                    pokemonImage.load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png") {
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
                    pokemonId.text = pokemon.id.toString()
                }
            }
    }

    companion object {
        private val POKEMON_COMPARATOR = object  : DiffUtil.ItemCallback<Pokemon>() {
            override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean =
                oldItem == newItem
        }
    }
}