package com.rafaelboban.pokedex.ui.type_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rafaelboban.pokedex.databinding.FragmentDamageBinding


class DamageFragment : Fragment() {

    lateinit var binding: FragmentDamageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDamageBinding.inflate(layoutInflater)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = DamageFragment()
    }
}