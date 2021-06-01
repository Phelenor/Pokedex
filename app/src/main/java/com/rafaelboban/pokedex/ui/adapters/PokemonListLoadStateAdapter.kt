package com.rafaelboban.pokedex.ui.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.LayoutSnackbarBinding
import com.rafaelboban.pokedex.databinding.PokemonItemLoadStateBinding

class PokemonListLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<PokemonListLoadStateAdapter.LoadStateViewHolder>() {
    var parent: ViewGroup? = null

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = PokemonItemLoadStateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        this.parent = parent
        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(private val binding: PokemonItemLoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetry.setOnClickListener {
                retry()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {

                loadProgressbar.isVisible = loadState is LoadState.Loading
                loadFetchingTv.isVisible = loadState is LoadState.Loading
                buttonRetry.isVisible = loadState !is LoadState.Loading

                if (loadState !is LoadState.Loading) {

                    binding.loadCard.strokeColor = parent!!.resources.getColor(R.color.error)

                    val sb = Snackbar.make(parent!!, "", Snackbar.LENGTH_LONG)
                    sb.view.setBackgroundColor(Color.TRANSPARENT);
                    val sbLayout = sb.view as Snackbar.SnackbarLayout
                    val sbBinding = LayoutSnackbarBinding.inflate(LayoutInflater.from(parent!!.context))
                    sbLayout.addView(sbBinding.root, 0)
                    sbBinding.message.text = parent!!.context.getString(R.string.check_connection)
                    sbBinding.message.backgroundTintList =
                        ColorStateList.valueOf(parent!!.context.resources.getColor(R.color.error))
                    sbBinding.snackbarClose.setOnClickListener {
                        sb.dismiss()
                    }
                    sb.show()
                } else {
                    binding.loadCard.strokeColor = parent!!.resources.getColor(R.color.tint_secondary)
                }

            }
        }
    }
}