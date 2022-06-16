package com.example.isthisahangout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.PokemonMovesDisplayLayoutBinding

class PokemonMovesAdapter :
    ListAdapter<String, PokemonMovesAdapter.PokemonMovesViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonMovesViewHolder =
        PokemonMovesViewHolder(
            PokemonMovesDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PokemonMovesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PokemonMovesViewHolder(
        private val binding: PokemonMovesDisplayLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(s: String) {
            binding.abilityNameTextView.text = s
        }
    }
}