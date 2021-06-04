package com.rafaelboban.pokedex.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.CardEvolutionItemBinding
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.model.TypeFull
import com.rafaelboban.pokedex.utils.Constants
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.getColor
import com.rafaelboban.pokedex.utils.getSprite

class EvolutionAdapter(
    private val evolutions: List<Pair<Pokemon, Int>>,
    private val types: List<TypeFull>
) :
    RecyclerView.Adapter<EvolutionAdapter.EvolutionViewHolder>() {

    inner class EvolutionViewHolder(val binding: CardEvolutionItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EvolutionAdapter.EvolutionViewHolder {
        val binding =
            CardEvolutionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EvolutionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EvolutionAdapter.EvolutionViewHolder, position: Int) {
        val evolution = evolutions[position]
        val preferences = holder.binding.root.context.getSharedPreferences(
            Constants.PREFERENCES_DEFAULT,
            Context.MODE_PRIVATE
        )

        if (position == 0) {
            holder.binding.evolutionMark.text =
                holder.binding.root.context.getString(R.string.unevolved)
            holder.binding.nextArrow.isVisible = false
            holder.binding.levelNum.isVisible = false
            holder.binding.evolutionCard.strokeColor =
                holder.binding.root.resources.getColor(R.color.cold_gray)
        }

        holder.binding.apply {
            pokemonImage.load(evolution.first.getSprite()) {
                placeholder(R.drawable.pokemon_placeholder)
                error(R.drawable.pokemon_placeholder_error)
            }

            val langId = preferences.getInt(Constants.LANGUAGE_KEY, 0)

            for (name in evolution.first.specieClass.names) {
                if (name.language.url.extractLangId() == langId) {
                    pokemonName.text = name.name.capitalize()
                    break
                } else if (Constants.LANG_ENGLISH_ID == langId) {
                    pokemonName.text = name.name.capitalize()
                }
            }

            val typesInfo = evolution.first.infoClass.types
            for (typeName in types) {
                if (typeName.name == typesInfo[0].type.name) {
                    for (name in typeName.names) {
                        if (name.language.url.extractLangId() == langId) {
                            typeFirstButton.text = name.name.capitalize()
                            typeFirstButton.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    root.context,
                                    typesInfo[0].type.getColor()
                                )
                            typeFirst.isVisible = true
                            break
                        } else if (Constants.LANG_ENGLISH_ID == langId) {
                            typeFirstButton.text = name.name.capitalize()
                            typeFirstButton.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    root.context,
                                    typesInfo[0].type.getColor()
                                )
                            typeFirst.isVisible = true
                        }
                    }
                }
                if (typesInfo.size > 1 && typeName.name == typesInfo[1].type.name) {
                    for (name in typeName.names) {
                        if (name.language.url.extractLangId() == langId) {
                            typeSecondButton.text = name.name.capitalize()
                            typeSecondButton.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    root.context,
                                    typesInfo[1].type.getColor()
                                )
                            typeSecond.isVisible = true
                            break
                        } else if (Constants.LANG_ENGLISH_ID == langId) {
                            typeSecondButton.text = name.name.capitalize()
                            typeSecondButton.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    root.context,
                                    typesInfo[1].type.getColor()
                                )
                            typeSecond.isVisible = true
                        }
                    }
                }

                if (typesInfo.size <= 1) typeTable.setColumnCollapsed(1, true)
            }

            if (evolution.second == 0) levelNum.isVisible = false
            else levelNum.text =
                root.context.getString(R.string.level_format, evolution.second.toString())

            when (position) {
                1 -> evolutionMark.text = root.context.getString(R.string.first_evolution)
                2 -> evolutionMark.text = root.context.getString(R.string.second_evolution)
            }

        }
    }

    override fun getItemCount() = evolutions.size

}
