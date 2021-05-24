package com.rafaelboban.pokedex.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rafaelboban.pokedex.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = ""

        binding.backButton.setOnClickListener {
            this.onBackPressed()
        }

    }
}