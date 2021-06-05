package com.rafaelboban.pokedex.ui

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.ActivityTypeBinding
import com.rafaelboban.pokedex.model.TypeFull
import com.rafaelboban.pokedex.ui.adapters.ViewPagerAdapter
import com.rafaelboban.pokedex.utils.Constants
import com.rafaelboban.pokedex.utils.Constants.LANG_ENGLISH_ID
import com.rafaelboban.pokedex.utils.extractLangId
import com.rafaelboban.pokedex.utils.getColor
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

lateinit var binding: ActivityTypeBinding
lateinit var type: TypeFull
var langId by Delegates.notNull<Int>()

@AndroidEntryPoint
class TypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTypeBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val preferences = getSharedPreferences(Constants.PREFERENCES_DEFAULT, Context.MODE_PRIVATE)
        langId = preferences.getInt(
            Constants.LANGUAGE_KEY,
            LANG_ENGLISH_ID
        )

        type = intent.getSerializableExtra(Constants.EXTRA_TYPE) as TypeFull

        binding.viewPager.adapter = ViewPagerAdapter(this, type)

        setupToolbar()

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "DAMAGE OVERVIEW"
                1 -> "MOVES"
                2 -> "POKEMON"
                else -> ""
            }
        }.attach()
    }

    private fun setupToolbar() {
        binding.backButton.setOnClickListener {
            this.onBackPressed()
        }

        supportActionBar?.title = ""
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(type.getColor())))
        supportActionBar?.elevation = 0.0F
        window.statusBarColor = resources.getColor(type.getColor())

        for (name in type.names) {
            if (name.language.url.extractLangId() == langId) {
                binding.title.text = if (LANG_ENGLISH_ID == langId) getString(
                    R.string.type_title,
                    name.name.capitalize()
                )
                else name.name.capitalize()
                break
            } else if (LANG_ENGLISH_ID == name.language.url.extractLangId()) {
                binding.title.text = getString(
                    R.string.type_title,
                    name.name.capitalize()
                )
            }
        }
        binding.tabLayout.backgroundTintList =
            ContextCompat.getColorStateList(this, type.getColor())
    }
}