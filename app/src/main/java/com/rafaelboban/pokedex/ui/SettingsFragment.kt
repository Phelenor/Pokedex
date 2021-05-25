package com.rafaelboban.pokedex.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.DialogClearBinding
import com.rafaelboban.pokedex.databinding.FragmentSettingsBinding
import com.rafaelboban.pokedex.databinding.LayoutSnackbarBinding
import com.rafaelboban.pokedex.ui.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.drawable.InsetDrawable

import android.graphics.drawable.ColorDrawable




@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel by viewModels<SettingsViewModel>()
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        setupListeners()

        return binding.root
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

    private fun showDialog() {
        val dialogBinding = DialogClearBinding.inflate(layoutInflater)
        val dialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window!!.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 16))

        val sb = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
        sb.view.setBackgroundColor(Color.TRANSPARENT);
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
