package com.rafaelboban.pokedex.ui.type_tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rafaelboban.pokedex.databinding.FragmentMoveBinding
import com.rafaelboban.pokedex.model.TypeFull

class MoveFragment(val type: TypeFull) : Fragment() {

    lateinit var binding: FragmentMoveBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoveBinding.inflate(layoutInflater)

        Log.d("TYPE", type.name)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(type: TypeFull) = MoveFragment(type)
    }
}