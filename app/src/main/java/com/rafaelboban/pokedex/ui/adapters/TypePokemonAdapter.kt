package com.rafaelboban.pokedex.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.CardPokemonSmallBinding
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.utils.Constants
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.getSprite

class TypePokemonAdapter(
    var pokemonList: List<Pokemon>,
    val onPokemonClick: (Pokemon) -> Unit
) : RecyclerView.Adapter<TypePokemonAdapter.PokemonViewHolder>() {


    inner class PokemonViewHolder(val binding: CardPokemonSmallBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TypePokemonAdapter.PokemonViewHolder {
        val binding =
            CardPokemonSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TypePokemonAdapter.PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position]

        val preferences = holder.binding.root.context.getSharedPreferences(
            Constants.PREFERENCES_DEFAULT,
            Context.MODE_PRIVATE
        )

        val langId = preferences.getInt(Constants.LANGUAGE_KEY, 0)

        holder.binding.root.setOnClickListener {
            onPokemonClick(pokemon)
        }

        holder.binding.apply {
            pokemonImage.load(pokemon.getSprite()) {
                placeholder(R.drawable.pokemon_placeholder)
                error(R.drawable.pokemon_placeholder_error)
            }
            pokemonName.text = pokemon.idClass.name.capitalize()
        }

        for (name in pokemon.specieClass.names) {
            if (name.language.url.extractLangId() == langId) {
                holder.binding.pokemonName.text = name.name.capitalize()
                break
            } else if (Constants.LANG_ENGLISH_ID == name.language.url.extractLangId()) {
                holder.binding.pokemonName.text = name.name.capitalize()
            }
        }
    }

    fun setPokemon(pokemon: List<Pokemon>) {
        pokemonList = pokemon
    }

    override fun getItemCount() = pokemonList.size

}
