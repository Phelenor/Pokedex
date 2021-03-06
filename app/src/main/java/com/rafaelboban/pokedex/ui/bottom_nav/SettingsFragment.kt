package com.rafaelboban.pokedex.ui.bottom_nav

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.DialogClearBinding
import com.rafaelboban.pokedex.databinding.FragmentSettingsBinding
import com.rafaelboban.pokedex.databinding.LayoutSnackbarBinding
import com.rafaelboban.pokedex.ui.AboutActivity
import com.rafaelboban.pokedex.ui.viewmodels.SettingsViewModel
import com.rafaelboban.pokedex.utils.Constants
import com.rafaelboban.pokedex.utils.Constants.LANGUAGE_KEY
import com.rafaelboban.pokedex.utils.Constants.LANGUAGE_NAME
import com.rafaelboban.pokedex.utils.Constants.LANG_ENGLISH_NAME
import com.rafaelboban.pokedex.utils.extractLangId
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel by viewModels<SettingsViewModel>()
    private lateinit var binding: FragmentSettingsBinding
    private var languages = listOf<String>()
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        preferences =
            activity?.getSharedPreferences(Constants.PREFERENCES_DEFAULT, Context.MODE_PRIVATE)!!

        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun saveLanguagePreferences(id: Int, name: String = LANG_ENGLISH_NAME): Boolean {
        if (preferences.getInt(LANGUAGE_KEY, 0) == id) {
            return false
        } else {
            with(preferences.edit()) {
                putInt(LANGUAGE_KEY, id)
                putString(LANGUAGE_NAME, name)
                apply()
                return true
            }
        }
    }

    private fun setupListeners() {
        binding.clearFavoritesButton.setOnClickListener {
            showDialog()
        }

        binding.infoButton.setOnClickListener {
            val intent = Intent(context, AboutActivity::class.java)
            context?.startActivity(intent)
        }
    }

    private fun setupSpinners() {
        val currentLanguage =
            preferences.getString(LANGUAGE_NAME, "")
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, languages)
        binding.languageMenu.setAdapter(arrayAdapter)
        binding.languageMenu.setText(currentLanguage, false)

        (binding.tilLanguage.editText as AutoCompleteTextView).onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedLanguage = arrayAdapter.getItem(position)
                for (language in viewModel.languages.value!!) {
                    if (language.nameEnglish == selectedLanguage) {
                        if (saveLanguagePreferences(
                                language.url.extractLangId(),
                                language.nameEnglish!!
                            )
                        ) {
                            binding.languageMenu.setText(language.nameEnglish, false)
                        } else if (language.nameNative == selectedLanguage) {
                            binding.languageMenu.setText(language.nameNative, false)
                        }
                    }
                }
            }
    }

    private fun setupObservers() {
        viewModel.languages.observe(viewLifecycleOwner) { language ->
            languages = language.map {
                val native = it.nameNative
                it.nameEnglish ?: native!!
            }.sorted()
            setupSpinners()
        }
    }

    private fun showDialog() {
        val dialogBinding = DialogClearBinding.inflate(layoutInflater)
        val dialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window!!.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 16))

        val sb = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
        sb.view.setBackgroundColor(Color.TRANSPARENT)
        val sbLayout = sb.view as Snackbar.SnackbarLayout
        val sbBinding = LayoutSnackbarBinding.inflate(layoutInflater)
        sbLayout.addView(sbBinding.root, 0)
        sbBinding.message.text = getString(R.string.favorites_is_clear)

        sbBinding.snackbarClose.setOnClickListener {
            sb.dismiss()
        }

        dialogBinding.clearButton.setOnClickListener {
            viewModel.clearFavorites()
            sb.show()
            dialog.dismiss()
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
