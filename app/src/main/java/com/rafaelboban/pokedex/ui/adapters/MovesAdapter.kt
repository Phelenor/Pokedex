package com.rafaelboban.pokedex.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.pokedex.R
import com.rafaelboban.pokedex.databinding.ItemMoveTableBinding
import com.rafaelboban.pokedex.model.MoveInfo
import com.rafaelboban.pokedex.utils.*

class MovesAdapter(
    var moves: List<MoveInfo>
) : RecyclerView.Adapter<MovesAdapter.MoveViewHolder>() {

    inner class MoveViewHolder(val binding: ItemMoveTableBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovesAdapter.MoveViewHolder {
        val binding =
            ItemMoveTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovesAdapter.MoveViewHolder, position: Int) {
        val move = moves[position]

        holder.binding.apply {

            if (move.damage_class != null) {
                tvCategory.text = move.damage_class.name.capitalize()
                containerCategory.setBackgroundColor(root.resources.getColor(move.getCategoryTint()))
            } else {
                tvCategory.text = root.resources.getString(R.string.varies)
                containerCategory.setBackgroundColor(root.resources.getColor(R.color.success))
            }

            containerCategory.background.alpha = 102

            if (move.power == 0) {
                tvPower.text = "-"
            } else {
                tvPower.text = move.power.toString()
            }

            tvPp.text = root.resources.getString(
                R.string.pp_format,
                move.pp.toString(),
                move.getMaxPP().toString()
            )

            val preferences = root.context.getSharedPreferences(
                Constants.PREFERENCES_DEFAULT,
                Context.MODE_PRIVATE
            )

            val langId = preferences.getInt(Constants.LANGUAGE_KEY, 0)

            for (name in move.names) {
                if (name.language.url.extractLangId() == langId) {
                    tvMove.text = name.name.capitalize()
                    break
                } else if (Constants.LANG_ENGLISH_ID == name.language.url.extractLangId()) {
                    tvMove.text = name.name.capitalize()
                }
            }

            tvGen.text = move.generation.name.substringAfter("generation-").uppercase()
            containerGen.setBackgroundColor(root.resources.getColor(move.getGenerationTint()))
            containerGen.background.alpha = 102

        }
    }

    fun setMovesList(moves: List<MoveInfo>) {
        this.moves = moves
        notifyDataSetChanged()
    }

    override fun getItemCount() = moves.size

}
