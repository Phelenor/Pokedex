package com.rafaelboban.pokedex.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rafaelboban.pokedex.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar!!.title = ""

        binding.backButton.setOnClickListener {
            this.onBackPressed()
        }
    }
}