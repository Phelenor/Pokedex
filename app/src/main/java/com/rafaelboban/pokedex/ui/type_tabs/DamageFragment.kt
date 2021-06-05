package com.rafaelboban.pokedex.ui.type_tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.FragmentDamageBinding
import com.rafaelboban.pokedex.model.Type
import com.rafaelboban.pokedex.model.TypeFull
import com.rafaelboban.pokedex.ui.TypeActivity
import com.rafaelboban.pokedex.ui.langId
import com.rafaelboban.pokedex.ui.viewmodels.TypeViewModel
import com.rafaelboban.pokedex.utils.Constants
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.getColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DamageFragment(val type: TypeFull) : Fragment() {

    lateinit var binding: FragmentDamageBinding
    val viewModel: TypeViewModel by activityViewModels()

    lateinit var types: List<TypeFull>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDamageBinding.inflate(layoutInflater)
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.types.observe(viewLifecycleOwner) {
            types = it
            displayOffensiveDamage()
            displayDefensiveDamage()
        }
    }

    private fun displayOffensiveDamage() {
        displayDoubleDamageTo()
        displayHalfDamageTo()
        displayNoDamageTo()
    }

    private fun displayDefensiveDamage() {
        displayDoubleDamageFrom()
        displayHalfDamageFrom()
        displayNoDamageFrom()
    }

    private fun setupTypeButtons(typeButton: MaterialButton, toTypes: List<Type>, typeIndex: Int): Int {
        if (typeIndex < toTypes.size) {
            for (typeFull in types) {
                if (typeFull.name == toTypes[typeIndex].name) {

                    typeButton.setOnClickListener {
                        onTypeClick(typeFull)
                    }

                    for (name in typeFull.names) {
                        if (name.language.url.extractLangId() == langId) {
                            typeButton.text = name.name.capitalize()
                            typeButton.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    requireContext(),
                                    toTypes[typeIndex].getColor()
                                )
                            typeButton.isVisible = true
                            break
                        } else if (Constants.LANG_ENGLISH_ID == name.language.url.extractLangId()) {
                            typeButton.text = name.name.capitalize()
                            typeButton.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    requireContext(),
                                    toTypes[typeIndex].getColor()
                                )
                            typeButton.isVisible = true
                        }
                    }
                }
            }
        }
        return typeIndex + 1
    }

    private fun displayNoDamageTo() {
        val toTypes = type.damage_relations.no_damage_to
        var typeIndex = 0

        binding.cardDamage0x.opaqueBar.setBackgroundColor(resources.getColor(R.color.cold_gray))
        binding.cardDamage0x.root.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.cold_gray)
        binding.cardDamage0x.root.background.alpha = 50
        binding.cardDamage0x.damageMultiplier.text = resources.getString(R.string.zero_multiplier)

        if (toTypes.isEmpty()) {
            binding.cardDamage0x.emptyState.isVisible = true
            return
        }


        for (button in binding.cardDamage0x.flexbox.children) {
            val typeButton = button as MaterialButton

            typeIndex = setupTypeButtons(typeButton, toTypes, typeIndex)
        }
    }

    private fun displayHalfDamageTo() {
        val toTypes = type.damage_relations.half_damage_to
        var typeIndex = 0

        binding.cardDamage05x.opaqueBar.setBackgroundColor(resources.getColor(R.color.error))
        binding.cardDamage05x.root.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.error)
        binding.cardDamage05x.root.background.alpha = 50
        binding.cardDamage05x.damageMultiplier.text = resources.getString(R.string.half_multiplier)

        if (toTypes.isEmpty()) {
            binding.cardDamage05x.emptyState.isVisible = true
            return
        }

        for (button in binding.cardDamage05x.flexbox.children) {
            val typeButton = button as MaterialButton

            typeIndex = setupTypeButtons(typeButton, toTypes, typeIndex)
        }
    }

    private fun displayDoubleDamageTo() {
        val toTypes = type.damage_relations.double_damage_to
        var typeIndex = 0

        binding.cardDamage2x.damageMultiplier.text = resources.getString(R.string.double_multiplier)

        if (toTypes.isEmpty()) {
            binding.cardDamage2x.emptyState.isVisible = true
            return
        }


        for (button in binding.cardDamage2x.flexbox.children) {
            val typeButton = button as MaterialButton

            typeIndex = setupTypeButtons(typeButton, toTypes, typeIndex)
        }
    }

    private fun displayNoDamageFrom() {
        val toTypes = type.damage_relations.no_damage_from
        var typeIndex = 0

        binding.cardDefence0x.opaqueBar.setBackgroundColor(resources.getColor(R.color.cold_gray))
        binding.cardDefence0x.root.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.cold_gray)
        binding.cardDefence0x.root.background.alpha = 50
        binding.cardDefence0x.damageMultiplier.text = resources.getString(R.string.zero_multiplier)

        if (toTypes.isEmpty()) {
            binding.cardDefence0x.emptyState.isVisible = true
            return
        }

        for (button in binding.cardDefence0x.flexbox.children) {
            val typeButton = button as MaterialButton

            typeIndex = setupTypeButtons(typeButton, toTypes, typeIndex)
        }
    }

    private fun displayHalfDamageFrom() {
        val toTypes = type.damage_relations.half_damage_from
        var typeIndex = 0

        binding.cardDefence05x.opaqueBar.setBackgroundColor(resources.getColor(R.color.error))
        binding.cardDefence05x.root.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.error)
        binding.cardDefence05x.root.background.alpha = 50
        binding.cardDefence05x.damageMultiplier.text = resources.getString(R.string.half_multiplier)

        if (toTypes.isEmpty()) {
            binding.cardDefence05x.emptyState.isVisible = true
            return
        }

        for (button in binding.cardDefence05x.flexbox.children) {
            val typeButton = button as MaterialButton

            typeIndex = setupTypeButtons(typeButton, toTypes, typeIndex)
        }
    }

    private fun displayDoubleDamageFrom() {
        val toTypes = type.damage_relations.double_damage_from
        var typeIndex = 0

        binding.cardDefence2x.damageMultiplier.text =
            resources.getString(R.string.double_multiplier)

        if (toTypes.isEmpty()) {
            binding.cardDefence2x.emptyState.isVisible = true
            return
        }

        for (button in binding.cardDefence2x.flexbox.children) {
            val typeButton = button as MaterialButton

            typeIndex = setupTypeButtons(typeButton, toTypes, typeIndex)
        }
    }

    private fun onTypeClick(type: TypeFull) {
        if (type.name != this.type.name) {
            val intent = Intent(requireContext(), TypeActivity::class.java).apply {
                putExtra(Constants.EXTRA_TYPE, type)
            }
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(type: TypeFull) = DamageFragment(type)
    }
}