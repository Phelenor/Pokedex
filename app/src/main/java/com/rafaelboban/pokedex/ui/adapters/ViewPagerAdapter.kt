package com.rafaelboban.pokedex.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rafaelboban.pokedex.ui.type_tabs.DamageFragment
import com.rafaelboban.pokedex.ui.type_tabs.MoveFragment
import com.rafaelboban.pokedex.ui.type_tabs.PokemonFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> DamageFragment.newInstance()
        1 -> MoveFragment.newInstance()
        2 -> PokemonFragment.newInstance()
        else -> MoveFragment.newInstance()
    }
}