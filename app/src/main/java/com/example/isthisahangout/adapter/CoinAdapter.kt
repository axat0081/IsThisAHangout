package com.example.isthisahangout.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.CoinDisplayLayoutBinding
import com.example.isthisahangout.models.cryptocoin.Coin

class CoinAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<Coin, CoinAdapter.CoinViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Coin>() {
            override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder =
        CoinViewHolder(
            CoinDisplayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnItemClickListener {
        fun onItemClick(coin: Coin)
    }

    inner class CoinViewHolder(
        private val binding: CoinDisplayLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position))
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(coin: Coin) {
            binding.apply {
                coinNameTextView.text = coin.name
                coinSymbolTextView.text = "Symbol - ${coin.symbol}"
                coinRankTextView.text = "Rank - ${coin.rank}"
            }
        }
    }
}