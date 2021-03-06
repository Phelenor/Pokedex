package com.rafaelboban.pokedex.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.CardPokemonItemBinding
import com.rafaelboban.pokedex.databinding.CardSeparatorItemBinding
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.UiModel
import com.rafaelboban.pokedex.utils.Constants
import com.rafaelboban.pokedex.utils.Constants.LANGUAGE_KEY
import com.rafaelboban.pokedex.utils.Constants.PREFERENCES_DEFAULT
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.getSprite
import com.rafaelboban.pokedex.utils.toRoman


class PokemonListAdapter(
    private val onFavoriteClick: (Pokemon) -> Unit,
    private val onPokemonClick: (Pokemon) -> Unit
) :
    PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(POKEMON_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.card_separator_item) {
            SeparatorViewHolder(
                CardSeparatorItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            PokemonViewHolder(
                CardPokemonItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.PokemonItem -> R.layout.card_pokemon_item
            is UiModel.SeparatorItem -> R.layout.card_separator_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.PokemonItem -> (holder as PokemonViewHolder).bind(uiModel.pokemon)
                is UiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(uiModel.description)
            }
        }
    }

    inner class PokemonViewHolder(private val binding: CardPokemonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: Pokemon) {
            val pokemonBase = pokemon.idClass
            val preferences =
                binding.root.context.getSharedPreferences(PREFERENCES_DEFAULT, Context.MODE_PRIVATE)
            binding.apply {
                pokemonImage.load(pokemon.getSprite()) {
                    placeholder(R.drawable.pokemon_placeholder)
                    error(R.drawable.pokemon_placeholder_error)
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

                val langId = preferences.getInt(LANGUAGE_KEY, 0)

                for (name in pokemon.specieClass.names) {
                    if (name.language.url.extractLangId() == langId) {
                        pokemonName.text = name.name.capitalize()
                        break
                    } else if (Constants.LANG_ENGLISH_ID == name.language.url.extractLangId()) {
                        pokemonName.text = name.name.capitalize()
                    }
                }


                pokemonId.text = "%03d".format(pokemon.infoClass.id)

                binding.pokemonCard.setOnClickListener {
                    onPokemonClick(pokemon)
                }

                favoriteButton.setOnClickListener {
                    if (pokemonBase.isFavorite) {
                        onFavoriteClick(pokemon)

                        binding.favoriteButton.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                binding.root.resources,
                                R.drawable.ic_star_0, null
                            )
                        )
                    } else {
                        onFavoriteClick(pokemon)

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


    inner class SeparatorViewHolder(private val binding: CardSeparatorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(desc: String) {
            binding.separatorDescription.text =
                binding.root.context.getString(
                    R.string.generation_separator_text,
                    desc.toInt().toRoman()
                )
        }
    }


    companion object {
        private val POKEMON_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                (oldItem is UiModel.PokemonItem && newItem is UiModel.PokemonItem &&
                        oldItem.pokemon.idClass.name == newItem.pokemon.idClass.name) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem &&
                                oldItem.description == newItem.description)


            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }
}