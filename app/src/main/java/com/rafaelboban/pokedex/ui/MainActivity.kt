package com.rafaelboban.pokedex.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.ActivityMainBinding
import com.rafaelboban.pokedex.utils.Constants.LANGUAGE_KEY
import com.rafaelboban.pokedex.utils.Constants.LANGUAGE_NAME
import com.rafaelboban.pokedex.utils.Constants.LANG_ENGLISH_ID
import com.rafaelboban.pokedex.utils.Constants.LANG_ENGLISH_NAME
import com.rafaelboban.pokedex.utils.Constants.PREFERENCES_DEFAULT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDefaultLanguage()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_search, R.id.navigation_favorites, R.id.navigation_settings)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.hide()
    }

    private fun setupDefaultLanguage() {
        val preferences = getSharedPreferences(PREFERENCES_DEFAULT, Context.MODE_PRIVATE)
        with (preferences.edit()) {
            if (!preferences.contains(LANGUAGE_KEY)) {
                putInt(LANGUAGE_KEY, LANG_ENGLISH_ID)
                putString(LANGUAGE_NAME, LANG_ENGLISH_NAME)
                apply()
            }
        }
    }
}