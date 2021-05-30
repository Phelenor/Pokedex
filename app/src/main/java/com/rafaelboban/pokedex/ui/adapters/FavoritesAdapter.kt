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
import com.rafaelboban.pokedex.database.PokemonDao
import com.rafaelboban.pokedex.databinding.CardPokemonItemBinding
import com.rafaelboban.pokedex.model.Favorite
import com.rafaelboban.pokedex.ui.FavoritesFragment
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.getSprite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FavoritesAdapter(
    var pokemonList: List<Favorite>,
    private val pokemonDao: PokemonDao,
    private val fragmentReference: FavoritesFragment
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    var FAVORITES_EDIT_MODE = false

    inner class FavoritesViewHolder(val binding: CardPokemonItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding =
            CardPokemonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val pokemon = pokemonList[position]

        holder.binding.apply {
            pokemonImage.load(pokemon.pokemon.idClass.getSprite()) {
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

            if (pokemon.pokemon.idClass.isFavorite) {
                favoriteButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        root.resources,
                        R.drawable.ic_star_1, null
                    )
                )
            } else {
                favoriteButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        root.resources,
                        R.drawable.ic_star_0, null
                    )
                )
            }

            val sp = root.context.getSharedPreferences("default", Context.MODE_PRIVATE)
            val langId = sp.getInt("langId", 0)

            if (sp.getInt("langId", 0) == 9) {
                pokemonName.text = pokemon.pokemon.idClass.name.capitalize()
            } else {
                for (name in pokemon.pokemon.specieClass.names) {
                    if (name.language.url.extractLangId() == langId) {
                        pokemonName.text = name.name.capitalize()
                        break
                    }
                }
            }
            pokemonId.text = pokemon.pokemon.idClass.pokemonId.toString().padStart(3, '0')

            if (FAVORITES_EDIT_MODE) {
                reorderIcon.visibility = View.VISIBLE
            } else {
                reorderIcon.visibility = View.GONE
            }

            favoriteButton.setOnClickListener {
                if (pokemon.pokemon.idClass.isFavorite) {
                    pokemon.pokemon.idClass.isFavorite = false
                    pokemon.id = null

                    (pokemonList as ArrayList).remove(pokemon)
                    notifyItemRemoved(holder.bindingAdapterPosition)

                    CoroutineScope(Dispatchers.IO).launch {
                        pokemonDao.delete(pokemon.pokemon.idClass.name)
                    }

                    favoriteButton.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            root.resources,
                            R.drawable.ic_star_0, null
                        )
                    )
                } else {
                    pokemon.pokemon.idClass.isFavorite = true

                    CoroutineScope(Dispatchers.IO).launch {
                        pokemonDao.insert(Favorite(pokemon = pokemon.pokemon))
                    }

                    favoriteButton.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            root.resources,
                            R.drawable.ic_star_1, null
                        )
                    )
                }
            }

            reorderIcon.setOnTouchListener { v, event ->
                fragmentReference.startDragging(holder)
                true
            }

        }

    }

    override fun getItemCount() = pokemonList.size

    fun setPokemon(pokemon: List<Favorite>) {
        this.pokemonList = pokemon
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        Collections.swap(pokemonList, from, to)
    }
}