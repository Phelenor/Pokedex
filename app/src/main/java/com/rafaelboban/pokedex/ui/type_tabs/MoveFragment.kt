package com.rafaelboban.pokedex.ui.type_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rafaelboban.pokedex.databinding.FragmentMoveBinding

class MoveFragment : Fragment() {

    lateinit var binding: FragmentMoveBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoveBinding.inflate(layoutInflater)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MoveFragment()
    }
}