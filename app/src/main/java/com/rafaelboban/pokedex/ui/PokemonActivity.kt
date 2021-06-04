package com.rafaelboban.pokedex.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.ActivityPokemonBinding
import com.rafaelboban.pokedex.databinding.DialogPokemonBinding
import com.rafaelboban.pokedex.model.EvolutionChainInfo
import com.rafaelboban.pokedex.model.Pokemon
import com.rafaelboban.pokedex.ui.adapters.EvolutionAdapter
import com.rafaelboban.pokedex.ui.viewmodels.PokemonViewModel
import com.rafaelboban.pokedex.utils.Constants
import com.rafaelboban.pokedex.utils.Constants.EXTRA_POKEMON
import com.rafaelboban.pokedex.utils.Constants.KG_TO_LBS
import com.rafaelboban.pokedex.utils.Constants.LANG_ENGLISH_ID
import com.rafaelboban.pokedex.utils.Constants.METER_TO_IN
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.getColor
import com.rafaelboban.pokedex.utils.getSprite
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class PokemonActivity : AppCompatActivity() {

    private lateinit var pokemon: Pokemon
    private lateinit var pokemonEvolutionChain: EvolutionChainInfo
    private lateinit var binding: ActivityPokemonBinding
    private val viewModel by viewModels<PokemonViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)

        pokemon = intent.getSerializableExtra(EXTRA_POKEMON) as Pokemon
        viewModel.getEvolutionChain(pokemon)

        val preferences = getSharedPreferences(Constants.PREFERENCES_DEFAULT, Context.MODE_PRIVATE)
        val langId = preferences.getInt(
            Constants.LANGUAGE_KEY,
            LANG_ENGLISH_ID
        )


        for (name in pokemon.specieClass.names) {
            if (name.language.url.extractLangId() == langId) {
                binding.collapsingToolbar.title = name.name.capitalize()
                break
            } else if (LANG_ENGLISH_ID == langId) {
                binding.collapsingToolbar.title = name.name.capitalize()
            }
        }

        binding.backButton.setOnClickListener {
            this.onBackPressed()
        }

        if (pokemon.idClass.isFavorite) {
            binding.favoriteButton.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_star_1, null
                )
            )
        }

        binding.pokemonIcon.load(pokemon.idClass.getSprite()) {
            placeholder(R.drawable.pokemon_placeholder)
            error(R.drawable.pokemon_placeholder_error)
        }

        binding.pokedexNum.text = "%03d".format(pokemon.id)

        val weightKg = pokemon.infoClass.weight * 0.1
        val weightLbs = KG_TO_LBS * weightKg
        binding.weight.text =
            getString(R.string.weight_format, "%.1f".format(weightLbs), "%.1f".format(weightKg))

        val heightM = pokemon.infoClass.height / 10.0
        val heightInches = METER_TO_IN * heightM
        binding.height.text =
            getString(
                R.string.height_format,
                "%d".format((heightInches.roundToInt() / 12)),
                "%02d".format((heightInches.roundToInt() % 12)),
                "%.1f".format(heightM)
            )

        setupObservers()
        setupListeners()
        displayTypes()
        displayAbilityGrid()
        displayStatsCard()
        displayEvolutionChain()


        setContentView(binding.root)
    }

    private fun displayEvolutionChain() {
    }

    private fun setupObservers() {
        viewModel.evolutions.observe(this) {
            val evolutionField = binding.evolutionField

            val evolutions = it.filter { evolution ->
                evolution.size > 1
            }

            if (evolutions.isNotEmpty()) {
                evolutionField.visibility = View.VISIBLE
                binding.evolutionProgress.isVisible = false
            }
            else {
                evolutionField.visibility = View.GONE
                binding.evolutionProgress.isVisible = false
            }

            for (evolution in evolutions) {
                val recyclerView = RecyclerView(this)
                val adapter = EvolutionAdapter(evolution)
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                recyclerView.adapter = adapter

                val scale = resources.displayMetrics.density
                val dpAsPixels = (16 * scale + 0.5f).toInt()
                recyclerView.setPadding(dpAsPixels, 0, dpAsPixels, 0)
                recyclerView.clipToPadding = false
                evolutionField.addView(recyclerView)
            }
        }
    }


    private fun displayStatsCard() {
        val stats = pokemon.infoClass.stats

        binding.statsCard.apply {
            statHp.text = stats[0].base_stat.toString()
            progressHp.progress = stats[0].base_stat.toFloat()

            statAttack.text = stats[1].base_stat.toString()
            progressAttack.progress = stats[1].base_stat.toFloat()

            statDefense.text = stats[2].base_stat.toString()
            progressDefense.progress = stats[2].base_stat.toFloat()

            statSpAtk.text = stats[3].base_stat.toString()
            progressSpAtk.progress = stats[3].base_stat.toFloat()

            statSpDef.text = stats[4].base_stat.toString()
            progressSpDef.progress = stats[4].base_stat.toFloat()

            statSpeed.text = stats[5].base_stat.toString()
            progressSpeed.progress = stats[5].base_stat.toFloat()

            statTotal.text = stats.map { it.base_stat }.sum().toString()
        }
    }

    private fun displayAbilityGrid() {
        val abilities = pokemon.infoClass.abilities
        for (i in abilities.indices) {
            binding.apply {
                when (i) {
                    0 -> {
                        abilityGrid.ability1.isVisible = true
                        abilityGrid.ability1Hidden.isVisible = abilities[i].is_hidden
                        abilityGrid.ability1Name.text = abilities[i].ability.name.capitalize()
                    }
                    1 -> {
                        abilityGrid.ability2.isVisible = true
                        abilityGrid.ability2Hidden.isVisible = abilities[i].is_hidden
                        abilityGrid.ability2Name.text = abilities[i].ability.name.capitalize()
                    }
                    2 -> {
                        abilityGrid.ability3.isVisible = true
                        abilityGrid.ability3Hidden.isVisible = abilities[i].is_hidden
                        abilityGrid.ability3Name.text = abilities[i].ability.name.capitalize()
                    }
                }
            }
        }
    }

    private fun displayTypes() {
        val types = pokemon.infoClass.types

        binding.typeFirst.text = types[0].type.name.capitalize()
        binding.typeFirst.backgroundTintList =
            ContextCompat.getColorStateList(this, types[0].type.getColor())
        binding.typeFirst.isVisible = true

        if (types.size > 1) {
            binding.typeSecond.text = types[1].type.name.capitalize()
            binding.typeSecond.backgroundTintList =
                ContextCompat.getColorStateList(this, types[1].type.getColor())
            binding.typeSecond.isVisible = true
        }

    }

    private fun setupListeners() {

        binding.favoriteButton.setOnClickListener {
            if (pokemon.idClass.isFavorite) {
                viewModel.onFavoriteClick(pokemon)

                binding.favoriteButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        binding.root.resources,
                        R.drawable.ic_star_0, null
                    )
                )
            } else {
                viewModel.onFavoriteClick(pokemon)

                binding.favoriteButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        binding.root.resources,
                        R.drawable.ic_star_1, null
                    )
                )
            }
        }

        binding.flavorButton.setOnClickListener {
            val dialogBinding = DialogPokemonBinding.inflate(layoutInflater)
            val dialog: AlertDialog = AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .create()

            dialog.window!!.setBackgroundDrawable(
                InsetDrawable(
                    ColorDrawable(Color.TRANSPARENT),
                    128
                )
            )

            val langId =
                getSharedPreferences(Constants.PREFERENCES_DEFAULT, Context.MODE_PRIVATE).getInt(
                    Constants.LANGUAGE_KEY,
                    LANG_ENGLISH_ID
                )

            var firstEnglishFlavor = true
            for (flavor in pokemon.specieClass.flavor_text_entries) {
                if (langId == flavor.language.url.extractLangId()) {
                    dialogBinding.dialogText.text = flavor.flavor_text
                    break
                } else if (flavor.language.url.extractLangId() == LANG_ENGLISH_ID && firstEnglishFlavor) {
                    dialogBinding.dialogText.text = flavor.flavor_text
                    firstEnglishFlavor = false
                }
            }

            dialogBinding.okButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

    }
}