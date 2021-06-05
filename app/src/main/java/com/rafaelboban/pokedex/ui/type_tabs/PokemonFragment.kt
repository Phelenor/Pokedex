package com.rafaelboban.pokedex.ui.type_tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rafaelboban.pokedex.databinding.FragmentPokemonBinding
import com.rafaelboban.pokedex.model.TypeFull


class PokemonFragment(val type: TypeFull) : Fragment() {

    lateinit var binding: FragmentPokemonBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPokemonBinding.inflate(layoutInflater)

        Log.d("TYPE", type.name)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(type: TypeFull) = PokemonFragment(type)
    }
}