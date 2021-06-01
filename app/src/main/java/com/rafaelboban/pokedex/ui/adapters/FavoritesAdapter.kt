package com.rafaelboban.pokedex.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.CardPokemonItemBinding
import com.rafaelboban.pokedex.model.Favorite
import com.rafaelboban.pokedex.utils.Constants.LANGUAGE_KEY
import com.rafaelboban.pokedex.utils.Constants.LANG_ENGLISH_ID
import com.rafaelboban.pokedex.utils.Constants.PREFERENCES_DEFAULT
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.getSprite
import java.util.*

class FavoritesAdapter(
    var favorites: List<Favorite>,
    private val onFavoriteClick: (Favorite) -> Unit,
    private val onFavoritesEdit: (RecyclerView.ViewHolder) -> Boolean
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    var favoritesModeEdit = false

    inner class FavoritesViewHolder(val binding: CardPokemonItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding =
            CardPokemonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val favorite = favorites[position]

        holder.binding.apply {
            pokemonImage.load(favorite.pokemon.idClass.getSprite()) {
                placeholder(R.drawable.pokemon_placeholder)
                error(R.drawable.pokemon_placeholder_error)
            }

            if (favorite.pokemon.idClass.isFavorite) {
                favoriteButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        root.resources,
                        R.drawable.ic_star_1, null
                    )
                )
            }

            val preferences = root.context.getSharedPreferences(PREFERENCES_DEFAULT, Context.MODE_PRIVATE)
            val languageId = preferences.getInt(LANGUAGE_KEY, 0)

            if (preferences.getInt(LANGUAGE_KEY, 0) == LANG_ENGLISH_ID) {
                pokemonName.text = favorite.pokemon.idClass.name.capitalize()
            } else {
                for (name in favorite.pokemon.specieClass.names) {
                    if (name.language.url.extractLangId() == languageId) {
                        pokemonName.text = name.name.capitalize()
                        break
                    }
                }
            }
            pokemonId.text = favorite.pokemon.idClass.pokemonId.toString().padStart(3, '0')

            if (favoritesModeEdit) {
                reorderIcon.visibility = View.VISIBLE
            } else {
                reorderIcon.visibility = View.GONE
            }

            favoriteButton.setOnClickListener {
                if (favorite.pokemon.idClass.isFavorite) {
                    favorite.pokemon.idClass.isFavorite = false
                    favorite.id = null

                    (favorites as ArrayList).remove(favorite)
                    notifyItemRemoved(holder.bindingAdapterPosition)

                    onFavoriteClick(favorite)

                    favoriteButton.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            root.resources,
                            R.drawable.ic_star_0, null
                        )
                    )
                }
            }

            reorderIcon.setOnTouchListener { _, _ -> onFavoritesEdit(holder) }
        }
    }

    override fun getItemCount() = favorites.size

    fun setPokemon(pokemon: List<Favorite>) {
        this.favorites = pokemon
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        Collections.swap(favorites, from, to)
    }
}